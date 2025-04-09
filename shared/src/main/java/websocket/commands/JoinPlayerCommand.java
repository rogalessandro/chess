package websocket.commands;

public class JoinPlayerCommand extends UserGameCommand {
    public JoinPlayerCommand(String authToken, int gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
