package server;

import websocket.commands.UserGameCommand;

public class JoinObserverCommand extends UserGameCommand {
    public JoinObserverCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
