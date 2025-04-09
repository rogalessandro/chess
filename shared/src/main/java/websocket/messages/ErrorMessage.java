package websocket.messages;

public class ErrorMessage extends ServerMessage {

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        setErrorMessage(errorMessage);
    }
}
