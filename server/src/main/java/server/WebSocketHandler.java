
package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import websocket.commands.JoinObserverCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.ResignCommand;
import servicefiles.JoinGameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import chess.ChessGame;
import chess.ChessMove;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private static final Map<Integer, Set<Session>> GAME_SESSIONS = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private record GameContext(MySQLAuthDAO authDAO, MySQLGameDAO gameDAO, GameData gameData, String username) {}



    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String commandType = json.get("commandType").getAsString();

            UserGameCommand command;

            switch (commandType) {
                case "CONNECT":
                    command = gson.fromJson(message, UserGameCommand.class);
                    handleConnect(command, session);
                    break;
                case "MAKE_MOVE":
                    command = gson.fromJson(message, MakeMoveCommand.class);
                    handleMakeMove(command, session);
                    break;
                case "LEAVE":
                    command = gson.fromJson(message, LeaveCommand.class);
                    handleLeave(command, session);
                    break;
                case "RESIGN":
                    command = gson.fromJson(message, ResignCommand.class);
                    handleResign(command, session);
                    break;
                case "JOIN_OBSERVER":
                    command = gson.fromJson(message, JoinObserverCommand.class);
                    handleJoinObserver(command, session);
                    break;
                default:
                    sendError(session, "Unknown command type: " + commandType);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error: " + e.getMessage());
        }
    }



    private void handleConnect(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        GAME_SESSIONS.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);

        try {
            var gameDAO = new MySQLGameDAO();
            var authDAO = new MySQLAuthDAO();
            var joinService = new JoinGameService(gameDAO, authDAO);
            GameData gameData = joinService.joinGame(authToken, gameID, null);

            send(session, new LoadGameMessage(gameData));

            String username = authDAO.getAuth(authToken).username();
            String note = username + " is observing el juego.";
            broadcastExcept(gameID, session, new NotificationMessage(note));
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }


    private void handleMakeMove(UserGameCommand baseCommand, Session session) {
        MakeMoveCommand command = (MakeMoveCommand) baseCommand;
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        var context = setupGameContext(gameID, authToken, session);
        if (context == null) return;

        try {
            GameData gameData = context.gameData;
            ChessGame game = gameData.game();
            String username = context.username();
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();

            if (game.isGameOver()) {
                sendError(session, "Error: Game not found");
                return;
            }

            ChessMove move = command.getMove();
            if (move == null) {
                sendError(session, "Error: Invalid move - move was null");
                return;
            }

            ChessGame.TeamColor playerColor =
                    username.equals(whiteUsername) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            if (playerColor != game.getTeamTurn()) {
                sendError(session, "Error: It is not your turn");
                return;
            }

            try {
                game.makeMove(move);
            } catch (Exception e) {
                sendError(session, "Error: Invalid move - " + e.getMessage());
                return;
            }

            context.gameDAO.updateGame(gameID, game);
            gameData = context.gameDAO.getGame(gameID); // get latest state

            LoadGameMessage loadGame = new LoadGameMessage(gameData);
            send(session, loadGame);
            broadcastExcept(gameID, session, loadGame);

            String note = username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition();
            broadcastExcept(gameID, session, new NotificationMessage(note));

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }



    private void handleLeave(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        // Clean up session tracking first
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                GAME_SESSIONS.remove(gameID);
            }
        }

        var context = setupGameContext(gameID, authToken, session);
        if (context == null) return;

        try {
            context.gameDAO.removePlayer(gameID, context.username());
            broadcastAll(gameID, new NotificationMessage(context.username() + " left the game."));
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }




    private void handleResign(UserGameCommand baseCommand, Session session) {
        ResignCommand command = (ResignCommand) baseCommand;
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        var context = setupGameContext(gameID, authToken, session);
        if (context == null) return;

        try {
            String username = context.username();
            String white = context.gameData.whiteUsername();
            String black = context.gameData.blackUsername();
            ChessGame game = context.gameData.game();

            if (game.isGameOver()) {
                sendError(session, "Error: Game not found");
                return;
            }

            if (username.equals("observer")) {
                sendError(session, "Error: You are not a player in this game");
                return;
            }

            game.setGameOver(true);
            context.gameDAO.updateGame(gameID, game);

            String message = username + " resigned the game.";
            broadcastAll(gameID, new NotificationMessage(message));

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }






    private void handleJoinObserver(UserGameCommand command, Session session) {
        try {
            var authDAO = new MySQLAuthDAO();
            var gameDAO = new MySQLGameDAO();

            var auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            joinGameSession(command.getGameID(), session);
            send(session, new LoadGameMessage(gameData));

        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }


    private void joinGameSession(Integer gameID, Session session) {
        GAME_SESSIONS.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }


    private void send(Session session, ServerMessage message) {
        try {
            if (session != null && session.isOpen()) {
                session.getRemote().sendString(gson.toJson(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendError(Session session, String errorMsg) {
        send(session, new ErrorMessage("Error: " + errorMsg));
    }

    private void broadcastAll(int gameID, ServerMessage message) {
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions == null) {return;}

        for (Session sess : sessions) {
            if (sess != null && sess.isOpen()) {
                send(sess, message);
            }
        }
    }


    private void broadcastExcept(int gameID, Session excluded, ServerMessage message) {
        Set<Session> sessions = GAME_SESSIONS.get(gameID);
        if (sessions == null) {return;}

        for (Session sess : sessions) {
            if (!sess.equals(excluded) && sess.isOpen()) {
                send(sess, message);
            }
        }
    }


    private GameContext setupGameContext(int gameID, String authToken, Session session) {
        try {
            var authDAO = new MySQLAuthDAO();
            var gameDAO = new MySQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return null;
            }

            var gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return null;
            }

            return new GameContext(authDAO, gameDAO, gameData, auth.username());
        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
            return null;
        }
    }


}