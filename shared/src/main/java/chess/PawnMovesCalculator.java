package chess;
import java.util.ArrayList;
import java.util.Collection;
import chess.ChessPosition;



public class PawnMovesCalculator implements PieceMovesCalculator{


     public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor){
         Collection<ChessMove> moves = new ArrayList<>();


        ChessPiece positionTO;


        int direction;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        ChessPosition adelante = new ChessPosition(position.getRow() + direction, position.getColumn());
        if (limiteTablero(position.getRow() + direction, position.getColumn()) && board.getPiece(adelante) == null){
            moves.add(new ChessMove(position, adelante, null));

            if ((teamColor == ChessGame.TeamColor.WHITE
                    && position.getRow() == 2) || (teamColor == ChessGame.TeamColor.BLACK
                    && position.getRow() == 7)){

                ChessPosition adelante2 = new ChessPosition(position.getRow() + 2 * direction, position.getColumn());
                if (board.getPiece(adelante2) == null) {
                    moves.add(new ChessMove(position, adelante2, null));
                }
            }

        }





        int [][] posiblesDireccionesDiagonal = {
                {direction, -1},
                {direction, 1}
        };

        for (int[] direccion : posiblesDireccionesDiagonal) {

            int nuevaRow = position.getRow() + direccion[0];
            int nuevaCol = position.getColumn() + direccion[1];


            if (limiteTablero(nuevaRow, nuevaCol)) {
                ChessPosition nuevaPosition = new ChessPosition(nuevaRow, nuevaCol);
                positionTO = board.getPiece(nuevaPosition);


                if (positionTO != null && positionTO.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, nuevaPosition, null));
                }
            }

        }




        Collection<ChessMove> newMoves = new ArrayList<>();
        for(ChessMove move: moves){
            if ((teamColor == ChessGame.TeamColor.WHITE
                    && move.getEndPosition().getRow() == 8)
                    || (teamColor == ChessGame.TeamColor.BLACK
                    && move.getEndPosition().getRow() == 1)) {

                ChessPiece.PieceType [] pieceTypes = {
                        ChessPiece.PieceType.BISHOP,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.ROOK
                };
                for(ChessPiece.PieceType promotionPiece : pieceTypes){
                    newMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), promotionPiece));
                }


            }else{

                newMoves.add(move);

            }


        }



       return newMoves;
    }




    private boolean limiteTablero(int row, int col){
        return row > 0 && row <= 8 && col > 0 && col <= 8;
    }



}

