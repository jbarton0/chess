package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard realBoard;


    public ChessGame() {
        turn = TeamColor.WHITE;
        realBoard = new ChessBoard();
        realBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return turn; }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possibleMoves = realBoard.getPiece(startPosition).pieceMoves(realBoard, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            if (isMoveValid(move, startPosition, possibleMoves)) {
                valid.add(move);
            }
        }
        return valid;
    }

    private boolean isMoveValid(ChessMove move, ChessPosition startPos, Collection<ChessMove> possibleMoves) {
        ChessBoard cloned = (ChessBoard) realBoard.clone();
        cloned.addPiece(move.getEndPosition(), realBoard.getPiece(startPos));
        cloned.addPiece(startPos, null);
        if (!possibleMoves.contains(move) | isInCheckHelper(realBoard.getPiece(startPos).getTeamColor(), cloned)) {
            return false;
        }
        return true;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition pos = move.getStartPosition();
        ChessPiece piece = realBoard.getPiece(pos);
        if (realBoard.getPiece(pos) == null) {
            throw new InvalidMoveException("No piece in that position");
        }
        if ( piece.getTeamColor() != turn | !isMoveValid(move, pos, realBoard.getPiece(pos).pieceMoves(realBoard, pos))) {
            throw new InvalidMoveException("Invalid move");
        }
        else if (move.getPromotionPiece() != null) {
            realBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            realBoard.addPiece(pos, null);
        }
        else {
            realBoard.addPiece(move.getEndPosition(), piece);
            realBoard.addPiece(pos, null);
        }
        if (turn == TeamColor.BLACK) { turn = TeamColor.WHITE; }
        else if (turn == TeamColor.WHITE) { turn = TeamColor.BLACK; }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckHelper(teamColor, realBoard);
    }

    private boolean isInCheckHelper(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = findKing(teamColor, board);
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (checkForCheck(i, j, teamColor, kingPos, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForCheck(int i, int j, TeamColor teamColor, ChessPosition kingPos, ChessBoard board) {
        ChessPosition pos = new ChessPosition(i+1,j+1);
        if (board.getPiece(pos) == null) {
            return false;
        }

        if (board.getPiece(pos).getTeamColor() != teamColor) {
            Collection<ChessMove> possibleMoves = board.getPiece(pos).pieceMoves(board, pos);
            if (possibleMoves == null) {
                return false;
            }

            for (ChessMove move : possibleMoves) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                ChessPosition pos = new ChessPosition(i+1,j+1);
                if (board.getPiece(pos) != null && board.getPiece(pos).getPieceType() == ChessPiece.PieceType.KING
                        && board.getPiece(pos).getTeamColor() == teamColor) {
                    return pos;
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
        ChessPosition kingPos = findKing(teamColor, realBoard);
        if (!isInCheckHelper(teamColor, realBoard)) {
            return false;
        }
        boolean stillInCheck = true;

        Collection<ChessPosition> teamPieces = findAllTeamPieces(teamColor);
        for (ChessPosition piecePos : teamPieces) {
            for (ChessMove move : realBoard.getPiece(piecePos).pieceMoves(realBoard, piecePos)) {
                if (!checkmateHelper(teamColor, move)) {
                    stillInCheck = false;
                }
            }
        }
        return stillInCheck;
    }

    private boolean checkmateHelper(TeamColor teamColor, ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessBoard cloned = (ChessBoard) realBoard.clone();
        cloned.addPiece(move.getEndPosition(), realBoard.getPiece(startPos));
        cloned.addPiece(startPos, null);
        if (isInCheckHelper(realBoard.getPiece(startPos).getTeamColor(), cloned)) {
            return true;
        }
        return false;
    }

    private Collection<ChessPosition> findAllTeamPieces(TeamColor teamColor) {
        Collection<ChessPosition> team = new ArrayList<>();
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                ChessPosition pos = new ChessPosition(i+1,j+1);
                if (realBoard.getPiece(pos) != null && realBoard.getPiece(pos).getTeamColor() == teamColor) {
                    team.add(pos);
                }
            }
        }
        return team;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor, realBoard);
        Collection<ChessMove> possible = realBoard.getPiece(kingPos).pieceMoves(realBoard, kingPos);
        Collection<ChessMove> valid = validMoves(kingPos);
        boolean noValidMoves = true;

        for (ChessPosition teamMate : findTeamPieces(teamColor)) {
            if (!validMoves(teamMate).isEmpty()) {
                noValidMoves = false;
            }
        }

        if (valid.isEmpty() && !possible.isEmpty() && noValidMoves && !isInCheckHelper(teamColor, realBoard)) {
            return true;
        }
        return false;
    }

    private Collection<ChessPosition> findTeamPieces(TeamColor teamColor) {
        Collection<ChessPosition> team = new ArrayList<>();
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                ChessPosition pos = new ChessPosition(i+1,j+1);
                if (realBoard.getPiece(pos) != null && realBoard.getPiece(pos).getPieceType() != ChessPiece.PieceType.KING
                        && realBoard.getPiece(pos).getTeamColor() == teamColor) {
                    team.add(pos);
                }
            }
        }
        return team;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        realBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return realBoard; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(realBoard, chessGame.realBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, realBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn= " + turn +
                ", " + realBoard +
                '}';
    }
}
