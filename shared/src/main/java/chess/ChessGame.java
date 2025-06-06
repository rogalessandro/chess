package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor piezaTurno;
    private ChessBoard tablero;
    private boolean gameOver = false;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

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



        ChessPiece piece = tablero.getPiece(startPosition);


        if(piece == null){
            return new ArrayList<>();
        }

        Collection<ChessMove> pieceMoves = piece.pieceMoves(tablero, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for(ChessMove move : pieceMoves){

            ChessPiece piezaAtacada = tablero.getPiece(move.getEndPosition());

            tablero.addPiece(move.getEndPosition(), piece);
            tablero.addPiece(move.getStartPosition(), null);

            boolean isCheck = isInCheck(piece.getTeamColor());

            tablero.addPiece(move.getStartPosition(),piece);
            tablero.addPiece(move.getEndPosition(), piezaAtacada);


            if(!isCheck){
                validMoves.add(move);
            }

        }



        return validMoves;


    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = tablero.getPiece(move.getStartPosition());

        if(isInCheck(piezaTurno)){
            throw new InvalidMoveException("In check");
        }

        if(piece == null){
            throw new InvalidMoveException("Nothing there");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move)){
            throw new InvalidMoveException("No valid move");
        }

        if(piezaTurno != piece.getTeamColor()){
            throw new InvalidMoveException("Not your turn");
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
        ChessPosition kingLocation = findKingLocation(teamColor);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pieceLoc = new ChessPosition(i, j);
                ChessPiece piece = tablero.getPiece(pieceLoc);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(tablero, pieceLoc);

                    if (checkIfPawn(piece, pieceLoc, moves, kingLocation)) {
                        return true;

                    }
                }
            }
        }
        return false;
    }

    public boolean checkDirection(ChessPosition pieceLoc, int direction, ChessPosition kingLocation) {
        if (pieceLoc.getRow() + direction == kingLocation.getRow()) {
            if (pieceLoc.getColumn() > kingLocation.getColumn()) {
                return (pieceLoc.getColumn() - kingLocation.getColumn()) == 1;
            } else {
                return (kingLocation.getColumn() - pieceLoc.getColumn()) == 1;
            }
        }
        return false;
    }

    public boolean checkIfPawn(ChessPiece piece, ChessPosition pieceLoc, Collection<ChessMove> moves, ChessPosition kingLocation) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

            return checkDirection(pieceLoc, direction, kingLocation);
        } else {
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingLocation)) {
                    return true;
                }
            }
        }
        return false;
    }




    public ChessPosition findKingLocation(TeamColor teamColor) {

        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition pieceLoc = new ChessPosition(i,j);
                ChessPiece piece = tablero.getPiece(pieceLoc);
                if(piece != null){
                    if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                        return pieceLoc;
                    }
                }

            }
        }
        return null;
    }



    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoLegalMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false.
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && hasNoLegalMoves(teamColor);
    }

    /**
     * Checks if the given team has no valid moves left.
     *
     * @param teamColor The team to check.
     * @return True if the team has no legal moves, otherwise false.
     */
    private boolean hasNoLegalMoves(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pieceLoc = new ChessPosition(i, j);
                ChessPiece piece = tablero.getPiece(pieceLoc);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (canMoveWithoutCheck(piece, pieceLoc, teamColor, tablero)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean canMoveWithoutCheck(ChessPiece piece, ChessPosition pieceLoc, TeamColor teamColor, ChessBoard tablero) {
        Collection<ChessMove> moves = piece.pieceMoves(tablero, pieceLoc);

        for (ChessMove move : moves) {
            ChessPiece capturedPiece = tablero.getPiece(move.getEndPosition());


            tablero.addPiece(move.getEndPosition(), piece);
            tablero.addPiece(move.getStartPosition(), null);

            boolean isCheck = isInCheck(teamColor);


            tablero.addPiece(pieceLoc, piece);
            tablero.addPiece(move.getEndPosition(), capturedPiece);

            if (!isCheck) {
                return true;
            }
        }

        return false;
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



    private boolean limiteTablero(int row, int col){
        return row > 0 && row <= 8 && col > 0 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return piezaTurno == chessGame.piezaTurno && Objects.equals(tablero, chessGame.tablero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piezaTurno, tablero);
    }
}
