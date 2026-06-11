package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{

    public ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // pawns
        for (int i=0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        // the rest
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        for (int i : new int[]{0,7}) {
            board[i][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            board[i][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[i][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[i][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
            board[i][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
            board[i][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[i][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[i][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            color = ChessGame.TeamColor.BLACK;
        }
    }

    @Override
    protected Object clone() {
        ChessBoard cloned = new ChessBoard();

        for (int i=0; i<8; i++) {
            cloned.board[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return cloned;
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                Arrays.deepToString(board) +
                '}';
    }
}
