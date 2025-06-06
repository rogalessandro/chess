package chess;
import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;
public class KingMovesCalculator extends MovePiecesForward implements PieceMovesCalculator{
    public KingMovesCalculator() {
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        int[][] direccionesPosibles = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        return calculateMoves(board, position, teamColor, direccionesPosibles, ChessPiece.PieceType.KING);

    }

}