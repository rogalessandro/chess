import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@ServerEndpoint("/ws")
public class WebSocketHandler {
    private static final Map<Integer, Map<Session, String>> gameSessions = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(command, session);
                break;
            case MAKE_MOVE:
                handleMakeMove(command, session);
                break;
            case LEAVE:
                handleLeave(command, session);
                break;
            case RESIGN:
                handleResign(command, session);
                break;
        }
    }

    private void handleConnect(UserGameCommand command, Session session) {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        gameSessions.putIfAbsent(gameID, new ConcurrentHashMap<>());
        gameSessions.get(gameID).put(session, authToken);

    }



    @OnClose
    public void onClose(Session session) {
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}
