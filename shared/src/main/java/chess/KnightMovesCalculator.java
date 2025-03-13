package chess;
import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;
public class KnightMovesCalculator extends MovePiecesForward implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {

        int[][] direccionesPosibles = {
                {1, -2},
                {-1, -2},
                {2 , 1},
                {2, -1},
                {1, 2},
                {-1, 2},
                {-2, -1},
                {-2, 1}
        };

    return calculateMoves(board, position, teamColor, direccionesPosibles, ChessPiece.PieceType.KNIGHT);

    }

}