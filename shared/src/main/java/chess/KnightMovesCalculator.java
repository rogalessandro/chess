package chess;

import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;

public class KnightMovesCalculator implements PieceMovesCalculator{


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;

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
        return row > 0 && row <= 8 && col >0 && col <= 8;
    }

}