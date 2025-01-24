package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{



    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;

        int [][] direccionesPosibles = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}

        };

        for (int[] direccion : direccionesPosibles){

            int ciclo = 1;

            while(true){
                int nuevaRow = position.getRow() + direccion[0] * ciclo;
                int nuevaCol = position.getColumn() + direccion[1] * ciclo;

                if (!limiteTablero(nuevaRow, nuevaCol)){
                    break;
                }

                ChessPosition nuevaPosition = new ChessPosition(nuevaRow, nuevaCol);
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