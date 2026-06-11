package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_ROOK;

public class DrawBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int BORDER_WIDTH_IN_PADDED_CHARS = 1;
    private static final int BORDER_SIZE_IN_PADDED_CHARS = 10;

    public static String playingColor = "BLACK";

    private static boolean oddRow = true;

    public static void main(String[] args) {
        playingColor = args[0];
        ChessGame game = new Gson().fromJson(args[1], ChessGame.class);
        ChessBoard board = game.getBoard();

        var out = new PrintStream(System.out, false, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        out.println();
        drawBorderLine(out);

        if (playingColor.equals("BLACK")) {
            for (int i=0; i<8; i++) {
                printOneRow(i, out, board);
            }
        } else {
            for (int i=7; i>=0; i--) {
                printOneRow(i, out, board);
            }
        }

        drawBorderLine(out);
        out.print(RESET_TEXT_COLOR);
    }

    private static void printOneRow(int i, PrintStream out, ChessBoard board) {
        boolean isLight = true;
        ChessPiece[] row = board.board[i];
        setGreen(out);
        out.print(" " + Integer.toString(i+1) + " ");
        if (oddRow) {
            setLight(out);
            oddRow = false;
        } else {
            setDark(out);
            isLight = false;
            oddRow = true;
        }

        for (int j=0; j<8; j++) {
            ChessPiece p = row[j];
            String piece = getPiece(p);
            out.print(piece);
            if (isLight) {
                setDark(out);
                isLight = false;
            } else {
                setLight(out);
                isLight = true;
            }
        }
        setGreen(out);
        out.print(" " + Integer.toString(i+1) + " ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static String getPiece(ChessPiece piece) {
        if (piece == null) { return EMPTY; }
        var type = piece.getPieceType();
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            if (type.equals(ChessPiece.PieceType.KING)) { return WHITE_KING; }
            else if (type.equals(ChessPiece.PieceType.QUEEN)) { return WHITE_QUEEN; }
            else if (type.equals(ChessPiece.PieceType.KNIGHT)) { return WHITE_KNIGHT; }
            else if (type.equals(ChessPiece.PieceType.BISHOP)) { return WHITE_BISHOP; }
            else if (type.equals(ChessPiece.PieceType.ROOK)) { return WHITE_ROOK; }
            else if (type.equals(ChessPiece.PieceType.PAWN)) { return WHITE_PAWN; }
        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            if (type.equals(ChessPiece.PieceType.KING)) { return BLACK_KING; }
            else if (type.equals(ChessPiece.PieceType.QUEEN)) { return BLACK_QUEEN; }
            else if (type.equals(ChessPiece.PieceType.KNIGHT)) { return BLACK_KNIGHT; }
            else if (type.equals(ChessPiece.PieceType.BISHOP)) { return BLACK_BISHOP; }
            else if (type.equals(ChessPiece.PieceType.ROOK)) { return BLACK_ROOK; }
            else if (type.equals(ChessPiece.PieceType.PAWN)) { return BLACK_PAWN; }
        }
        return null;
    }

    private static void drawBorderLine(PrintStream out) {
        setGreen(out);
        ArrayList<String> charsWhite = new ArrayList<>(List.of("a","b","c","d","e","f","g","h"));
        ArrayList<String> charsBlack = new ArrayList<>(List.of("h","g","f","e","d","c","b","a"));

        out.print(EMPTY);
        if (playingColor.equals("WHITE")) {
            for (String c : charsWhite) {
                out.print(SMALL_SPACE + c + SMALL_SPACE);
            }
        } else {
            for (String c : charsBlack) {
                out.print(SMALL_SPACE + c + SMALL_SPACE);
            }
        }
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setDark(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setLight(PrintStream out) {
        out.print(SET_BG_COLOR_BEIGE);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }
}
