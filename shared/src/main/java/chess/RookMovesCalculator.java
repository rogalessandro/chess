package chess;
import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;
public class RookMovesCalculator extends MovePiecesForward implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        int[][] direccionesPosibles = {
                {0, -1}, {1, 0}, {0, 1}, {-1, 0}
        };


        return calculateMoves(board, position, teamColor, direccionesPosibles, ChessPiece.PieceType.ROOK);


    }

}