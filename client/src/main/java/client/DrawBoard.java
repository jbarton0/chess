package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.BLACK_ROOK;

public class DrawBoard {

    private boolean oddRow = true;

    public void drawIt(String playingColor, GameData gameData) {
        if (playingColor == null) { playingColor = "WHITE"; }
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();

        var out = new PrintStream(System.out, false, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        out.println();
        drawBorderLine(out, playingColor);

        if (playingColor.equals("BLACK")) {
            for (int i=0; i<8; i++) {
                printOneRow(i, out, board);
            }
        } else {
            for (int i=7; i>=0; i--) {
                printOneRow(i, out, board);
            }
        }

        drawBorderLine(out, playingColor);
        out.print(RESET_TEXT_COLOR);
    }

    private void printHighlightRow(int i, PrintStream out, ChessBoard board, Collection<String> validMoves, String chosen) {

        boolean isLight = true;
        ChessPiece[] row = firstHalf(isLight, out, board, i);

        for (int j=0; j<8; j++) {
            ChessPiece p = row[j];
            String piece = getPiece(p);
            String str = Integer.toString(i+1) + "," + Integer.toString(j+1);
            if (validMoves.contains(str) && isLight) {
                setHighLight(out);
            } else if (validMoves.contains(str) && !isLight) {
                setHighDark(out);
            } else if (str.equals(chosen)) {
                setHigh(out);
            }

            out.print(piece);
            if (isLight) {
                setDark(out);
                isLight = false;
            } else {
                setLight(out);
                isLight = true;
            }
        }
        secondHalf(out);
    }

    private ChessPiece[] firstHalf(boolean isLight, PrintStream out, ChessBoard board, int i){
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
        return row;
    }

    private void secondHalf(PrintStream out) {
        setGreen(out);
        out.print(" " + Integer.toString(i+1) + " ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    public void drawHighlight(String playingColor, GameData gameData, Collection<String> validMoves, String chosen) {
        if (playingColor == null) { playingColor = "WHITE"; }
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();

        var out = new PrintStream(System.out, false, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        out.println();
        drawBorderLine(out, playingColor);

        if (playingColor.equals("BLACK")) {
            for (int i=0; i<8; i++) {
                printHighlightRow(i, out, board, validMoves, chosen);
            }
        } else {
            for (int i=7; i>=0; i--) {
                printHighlightRow(i, out, board, validMoves, chosen);
            }
        }

        drawBorderLine(out, playingColor);
        out.print(RESET_TEXT_COLOR);
    }

    private void printOneRow(int i, PrintStream out, ChessBoard board) {
        boolean isLight = true;
        ChessPiece[] row = firstHalf(isLight, out, board, i);

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
        secondHalf(out);
    }

    private String getPiece(ChessPiece piece) {
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

    private void drawBorderLine(PrintStream out, String playingColor) {
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

    private void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void setDark(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void setLight(PrintStream out) {
        out.print(SET_BG_COLOR_BEIGE);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void setHighLight(PrintStream out) {
        out.print(SET_BG_COLOR_HIGHLIGHT_LIGHT);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void setHighDark(PrintStream out) {
        out.print(SET_BG_COLOR_HIGHLIGHT_DARK);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void setHigh(PrintStream out) {
        out.print(SET_BG_COLOR_HIGHLIGHT);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }
}
