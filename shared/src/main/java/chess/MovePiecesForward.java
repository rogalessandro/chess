package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovePiecesForward {

    private boolean limiteTablero(int row, int col){
        return row > 0 && row <= 8 && col >0 && col <= 8;
    }




    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position,
                                                ChessGame.TeamColor teamColor, int[][] direccionesPosibles,
                                                ChessPiece.PieceType type) {


        int rowPosition = position.getRow();
        int colPosition = position.getColumn();

        return switch (type) {
            case KING, KNIGHT -> flyAndAttack(position,direccionesPosibles,board ,teamColor);
            case QUEEN, ROOK, BISHOP -> moveAndAttack(position, direccionesPosibles, board, teamColor);
            default -> List.of();
        };

    }

    private Collection<ChessMove> flyAndAttack(ChessPosition position, int[][] direccionesPosibles,
                                               ChessBoard board, ChessGame.TeamColor teamColor) {


        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;

        for (int[] direccion : direccionesPosibles) {

            int nuevaRow = position.getRow() + direccion[0];
            int nuevaCol = position.getColumn() + direccion[1];


            if (limiteTablero(nuevaRow, nuevaCol)) {
                ChessPosition nuevaPosition = new ChessPosition(nuevaRow, nuevaCol);
                positionTO = board.getPiece(nuevaPosition);


                if (positionTO == null || positionTO.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, nuevaPosition, null));
                }
            }
        }

        return moves;

    }


    public Collection<ChessMove> moveAndAttack(ChessPosition position, int[][] direccionesPosibles,
                                               ChessBoard board, ChessGame.TeamColor teamColor){

        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece positionTO;


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

}
