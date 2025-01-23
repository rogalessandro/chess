package chess;

import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;

public class KingMovesCalculator implements PieceMovesCalculator{

    public KingMovesCalculator() {
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;

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

        for (int[] direccion : direccionesPosibles) {

            int newRow = position.getRow() + direccion[0];
            int newCol = position.getColumn() + direccion[1];


            if (limiteTablero(newRow, newCol)) {
                ChessPosition nuevaPosition = new ChessPosition(newRow, newCol);
                positionTO = board.getPiece(nuevaPosition);


                if (positionTO == null || positionTO.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, nuevaPosition, null));
                }
            }
        }

        return moves;

    }



    private boolean limiteTablero(int row, int col){
        return row > 0 && row < 8 && col >0 && col < 8;
    }


}