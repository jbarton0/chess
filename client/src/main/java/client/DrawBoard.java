package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_KING;

public class DrawBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int BORDER_WIDTH_IN_PADDED_CHARS = 1;
    private static final int BORDER_SIZE_IN_PADDED_CHARS = 10;

    public static String playingColor = "BLACK";

//    public DrawBoard(String color) {
//        playingColor = color;
//    }

    public static void main(String[] args) {

        playingColor = args[0];
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawBorderLine(out);
        drawTopTwo(out);

        if (playingColor.equals("WHITE")) {
            drawTwoEmptyLines(out, " 6 ", " 5 ");
            drawTwoEmptyLines(out, " 4 ", " 3 ");
        } else {
            drawTwoEmptyLines(out, " 3 ", " 4 ");
            drawTwoEmptyLines(out, " 5 ", " 6 ");
        }

        drawBottomTwo(out);
        drawBorderLine(out);
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

    private static void drawTwoEmptyLines(PrintStream out, String num1, String num2) {
        setGreen(out);
        out.print(num1);
        for (int i=1; i<=4; i++) {
            setLight(out);
            out.print(EMPTY);
            setDark(out);
            out.print(EMPTY);
        }
        setGreen(out);
        out.print(num1);
        out.print(RESET_BG_COLOR);
        out.println();

        setGreen(out);
        out.print(num2);
        for (int i=1; i<=4; i++) {
            setDark(out);
            out.print(EMPTY);
            setLight(out);
            out.print(EMPTY);
        }
        setGreen(out);
        out.print(num2);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawTopTwo(PrintStream out) {
        ArrayList<String> whites = new ArrayList<>(List.of(WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK));
        ArrayList<String> blacks = new ArrayList<>(List.of(BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK));

        boolean isLight = true;

        if (playingColor.equals("WHITE")) {
            setGreen(out);
            out.print(" 8 ");
            setLight(out);
            for (String piece : blacks) {
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
            out.print(" 8 ");
            out.print(RESET_BG_COLOR);
            out.println();

            setGreen(out);
            out.print(" 7 ");
            for (int i=1; i<=4; i++) {
                setDark(out);
                out.print(BLACK_PAWN);
                setLight(out);
                out.print(BLACK_PAWN);
            }
            setGreen(out);
            out.print(" 7 ");
            out.print(RESET_BG_COLOR);
            out.println();
        }

        else {
            setGreen(out);
            out.print(" 1 ");
            setLight(out);
            for (String piece : whites) {
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
            out.print(" 1 ");
            out.print(RESET_BG_COLOR);
            out.println();

            setGreen(out);
            out.print(" 2 ");
            for (int i=1; i<=4; i++) {
                setDark(out);
                out.print(WHITE_PAWN);
                setLight(out);
                out.print(WHITE_PAWN);
            }
            setGreen(out);
            out.print(" 2 ");
            out.print(RESET_BG_COLOR);
            out.println();
        }
    }

    private static void drawBottomTwo(PrintStream out) {
        ArrayList<String> whites = new ArrayList<>(List.of(WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK));
        ArrayList<String> blacks = new ArrayList<>(List.of(BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK));

        boolean isLight = true;

        if (playingColor.equals("WHITE")) {
            setGreen(out);
            out.print(" 2 ");
            for (int i=1; i<=4; i++) {
                setLight(out);
                out.print(WHITE_PAWN);
                setDark(out);
                out.print(WHITE_PAWN);
            }
            setGreen(out);
            out.print(" 2 ");
            out.print(RESET_BG_COLOR);
            out.println();

            setGreen(out);
            out.print(" 1 ");
            setDark(out);
            isLight = false;
            for (String piece : whites) {
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
            out.print(" 1 ");
            out.print(RESET_BG_COLOR);
            out.println();
        }

        else {
            setGreen(out);
            out.print(" 7 ");
            for (int i=1; i<=4; i++) {
                setLight(out);
                out.print(BLACK_PAWN);
                setDark(out);
                out.print(BLACK_PAWN);
            }
            setGreen(out);
            out.print(" 7 ");
            out.print(RESET_BG_COLOR);
            out.println();

            setGreen(out);
            out.print(" 8 ");
            setDark(out);
            isLight = false;
            for (String piece : blacks) {
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
            out.print(" 8 ");
            out.print(RESET_BG_COLOR);
            out.println();
        }
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
