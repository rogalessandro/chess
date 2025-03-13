package chess;
import java.util.ArrayList;
import java.util.Collection;
public class BishopMovesCalculator extends MovePiecesForward implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {

        int [][] direccionesPosibles = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        return calculateMoves(board, position, teamColor, direccionesPosibles, ChessPiece.PieceType.BISHOP);

    }

}