package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ui.BoardPrinter;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketFacade {
    private static final String SERVER_URI = "ws://localhost:8080/ws";
    private Session session;
    private final Gson gson = new Gson();
    private ChessGame currentGame;
    private int currentGameID;
    private ChessGame.TeamColor myColor;



    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(SERVER_URI));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to WebSocket server: " + e.getMessage(), e);
        }
    }

//    public void sendCommand(UserGameCommand command) {
//        if (session == null || !session.isOpen()) {
//            throw new IllegalStateException("WebSocket is not connected.");
//        }
//
//        String json = gson.toJson(command);
//        session.getAsyncRemote().sendText(json);
//    }
//
////    private void handleLoadGame(String rawMessage) {
////        LoadGameMessage gameMessage = gson.fromJson(rawMessage, LoadGameMessage.class);
////        ChessGame game = gameMessage.getGame();
////        ChessGame.TeamColor color = game.getTeamTurn(); // optional, for flipping the board if needed
////        BoardPrinter.drawBoard(game, color);
////    }
//
//
//    @OnOpen
//    public void onOpen(Session session) {
//        this.session = session;
//        System.out.println("Connected to WebSocket server.");
//    }
//
//    @OnMessage
//    public void onMessage(String message) {
//        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
//        switch (serverMessage.getServerMessageType()) {
//            case LOAD_GAME -> handleLoadGame(serverMessage);
//            case NOTIFICATION -> handleNotification(serverMessage);
//            case ERROR -> handleError(serverMessage);
//            default -> System.out.println("[UNKNOWN] " + message);
//        }
//    }
//
//
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        System.out.println("WebSocket closed: " + closeReason);
//        this.session = null;
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        System.err.println("WebSocket error: " + throwable.getMessage());
//    }
//
//    private void handleLoadGame(ServerMessage message) {
//        JsonObject json = gson.toJsonTree(message).getAsJsonObject();
//        JsonObject gameDataJson = json.getAsJsonObject("game");
//
//        ChessGame game = gson.fromJson(gameDataJson.get("game"), ChessGame.class);
//
//        // Optional: store for later moves
//        this.currentGame = game;
//
//        ChessGame.TeamColor color = game.getTeamTurn(); // Optional if you want board flipped
//        BoardPrinter.drawBoard(game, color);
//    }
//
//
//
//    private void handleNotification(ServerMessage message) {
//        System.out.println("Notification: " + message.getMessage());
//    }
//
//    private void handleError(ServerMessage message) {
//        System.out.println("Error: " + message.getErrorMessage());
//    }
//
//    private void send(UserGameCommand command) {
//        String json = gson.toJson(command);
//        session.getAsyncRemote().sendText(json);
//    }
//
//
//    public void joinObserver(int gameID, String authToken) {
//        this.currentGameID = gameID;
//        send(new JoinObserverCommand(authToken, gameID));
//    }
//
//
//    public void joinPlayer(int gameID, String authToken, ChessGame.TeamColor color) {
//        this.myColor = color;
//        this.currentGameID = gameID;
//        send(new JoinPlayerCommand(authToken, gameID));
//    }
//
//
//    public void makeMove(int gameID, String authToken, ChessMove move) {
//        send(new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move));
//
//    }
//
//    public void resign(int gameID, String authToken) {
//        send(new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
//    }
//
//    public void leave(int gameID, String authToken) {
//        send(new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
//    }
//
//






}





