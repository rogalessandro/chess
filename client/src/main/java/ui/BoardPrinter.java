package ui;

import chess.*;

public class BoardPrinter {

    public static void drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();

        int startRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int endRow = (perspective == ChessGame.TeamColor.WHITE) ? 0 : 9;
        int step = (startRow < endRow) ? 1 : -1;

        char[] columns;

        if (perspective == ChessGame.TeamColor.WHITE) {
            columns = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        } else {
            columns = new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        };

        System.out.println();

        System.out.print("   ");
        for (char col : columns) {
            System.out.print(" " + col + "\u2003" );
        }
        System.out.println();

        for (int row = startRow; row != endRow; row += step) {
            System.out.print(" " + row + " ");

            for (int colIndex = 0; colIndex < 8; colIndex++) {
                int col = columns[colIndex] - 'a' + 1;
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String pieceSymbol = getSymbol(piece);

                System.out.print(bgColor + pieceSymbol + EscapeSequences.RESET_TEXT_COLOR +
                        EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row);
        }

        System.out.print("   ");
        for (char col : columns) {
            System.out.print(" " + col + "\u2003");
        }
        System.out.println();
    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;

        return switch (piece.getPieceType()) {
            case KING   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_KING
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_KING;
            case QUEEN  -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_QUEEN
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_QUEEN;
            case ROOK   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_ROOK
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_BISHOP
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_KNIGHT
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_KNIGHT;
            case PAWN   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.WHITE_PAWN
                    : EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.BLACK_PAWN;
        };
    }
}
