package chess;

import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;

public class RookMovesCalculator implements PieceMovesCalculator{



    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;

        int [][] direccionesPosibles = {
                {0, -1},
                {1, 0},
                {0, 1},
                {-1, 0}

        };

        for (int[] direccion : direccionesPosibles){

            int ciclo = 1;

            while(true){
                int newRow = position.getRow() + direccion[0] * ciclo;
                int newCol = position.getColumn() + direccion[1] * ciclo;

                if (!limiteTablero(newRow, newCol)){
                    break;
                }

                ChessPosition nuevaPosition = new ChessPosition(newRow, newCol);
                positionTO = board.getPiece(nuevaPosition);

                if (positionTO == null){
                    moves.add(new ChessMove(position, nuevaPosition, null));
                }else{
                    if(positionTO.getTeamColor() != teamColor){
                        moves.add(new ChessMove(position, nuevaPosition, null));
                    }
                    break;
                }

                ciclo++;

            }




        }




        return moves;

    }



    private boolean limiteTablero(int row, int col){
        return row > 0 && row <= 8 && col >0 && col <= 8;
    }


}