// ChessWebSocketHandler.java
package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import server.JoinObserverCommand;
import server.LeaveCommand;
import server.ResignCommand;
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

    private static final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();



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
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);

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

        try {
            var authDAO = new MySQLAuthDAO();
            var gameDAO = new MySQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            ChessGame game = gameData.game();
            String username = auth.username();
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();


            if (game.isGameOver()) {
                send(session, new LoadGameMessage(gameData));
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

            gameDAO.updateGame(gameID, game);
            gameData = gameDAO.getGame(gameID);

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

        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameID);
            }
        }

        try {
            var authDAO = new MySQLAuthDAO();
            String username = authDAO.getAuth(authToken).username();
            broadcastAll(gameID, new NotificationMessage(username + " left the game."));
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }


    private void handleResign(UserGameCommand baseCommand, Session session) {
        ResignCommand command = (ResignCommand) baseCommand;
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            var authDAO = new MySQLAuthDAO();
            var gameDAO = new MySQLGameDAO();

            var auth = authDAO.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            String username = auth.username();
            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();
            ChessGame game = gameData.game();

            if (game.isGameOver()) {
                send(session, new LoadGameMessage(gameData));
                return;
            }

            gameData.game().setGameOver(true);
            gameDAO.updateGame(gameID, gameData.game());

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
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    private void removeGameSession(int gameID, Session session) {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameSessions.remove(gameID);
            }
        }
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
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) return;

        for (Session sess : sessions) {
            if (sess != null && sess.isOpen()) {
                send(sess, message);
            }
        }
    }


    private void broadcastExcept(int gameID, Session excluded, ServerMessage message) {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions == null) return;

        for (Session sess : sessions) {
            if (!sess.equals(excluded) && sess.isOpen()) {
                send(sess, message);
            }
        }
    }




}
