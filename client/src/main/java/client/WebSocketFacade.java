package client;

import chess.ChessGame;
import com.google.gson.Gson;

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


}





