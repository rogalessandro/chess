package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor piezaTurno;
    private ChessBoard tablero;


    public ChessGame() {
        this.tablero = new ChessBoard();
        tablero.resetBoard();
        this.piezaTurno = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return piezaTurno;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.piezaTurno = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieza = tablero.getPiece(startPosition);
        Collection<ChessMove> valids = pieza.pieceMoves(tablero, startPosition);


        return valids;


    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = tablero.getPiece(move.getStartPosition());

        if(piece == null){
            throw new InvalidMoveException("Nothing there");
        }

        if(piezaTurno != piece.getTeamColor()){
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move)){
            throw new InvalidMoveException("No valid move");
        }

        tablero.addPiece(move.getEndPosition(), piece);
        tablero.addPiece(move.getStartPosition(), null);

        if(move.getPromotionPiece() != null){
            ChessPiece promo = new ChessPiece(piezaTurno, move.getPromotionPiece());
            tablero.addPiece(move.getEndPosition(), promo);
        }


        if(piezaTurno == TeamColor.WHITE){
            piezaTurno = TeamColor.BLACK;
        }else{
            piezaTurno = TeamColor.WHITE;
        }




    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }


    public ChessPosition findKingLocation(){

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition pieceLoc = new ChessPosition(i,j);
                ChessPiece piece = tablero.getPiece(pieceLoc);

                if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == piezaTurno){

                }
            }
        }

        return new ChessPosition(0,0);
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.tablero = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return tablero;
    }
}
