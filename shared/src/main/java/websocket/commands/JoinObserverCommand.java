package websocket.commands;

public class JoinObserverCommand extends UserGameCommand {
    public JoinObserverCommand(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}

