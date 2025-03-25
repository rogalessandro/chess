import chess.*;
import client.ServerFacade;
import server.Server;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Server server = new Server();
        int port = server.run(8080);

        ServerFacade facade = new ServerFacade(port);

        ChessClient client = new ChessClient(facade);
        client.run();
    }
}