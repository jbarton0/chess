package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessPiece.PieceType myType;
    private final ChessGame.TeamColor myColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        myType = type;
        myColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return myColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return myType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // create base class that calculates piece moves (subclasses for each piece)
        // responsibility of ChessPiece class is to simply store type,color for chess piece
        // other responsibility of calculating piece moves is delegated to another set of classes

        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.KING) {
            return new KingMovesCalculator().getMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.QUEEN) {
            return new QueenMovesCalculator().getMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.KNIGHT) {
            return new KnightMovesCalculator().getMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.BISHOP) {
            return new BishopMovesCalculator().getMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.ROOK) {
            return new RookMovesCalculator().getMoves(board, myPosition);
        }

        if (piece.getPieceType() == PieceType.PAWN) {
            return new PawnMovesCalculator().getMoves(board, myPosition);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return myType == that.myType && myColor == that.myColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myType, myColor);
    }

    @Override
    public String toString() {
        return "ChessPiece{"  + myType + myColor +
                '}';
    }
}
