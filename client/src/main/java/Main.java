import chess.*;
import client.ServerFacade;
import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ServerFacade facade = new ServerFacade(8080);

        ChessClient client = new ChessClient(facade);
        client.run();
    }
}