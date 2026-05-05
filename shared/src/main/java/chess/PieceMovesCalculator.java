package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.List;

interface PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition);
}

class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow()-1;
        int col = myPosition.getColumn()-1;

        for (int i = col-1; i < col+2; i++) {
            for (int j = row-1; j < row+2; j++) {
                if (row == j && col == i) continue;

                if (i < 8 && i >= 0 && j >= 0 && j < 8) {
                    ChessPosition pos = new ChessPosition(j+1, i+1);
                    if (board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }
                }
            }
        }
        return moves;
    }
}

class QueenMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }
}

class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }
}

class BishopMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }
}

class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow()-1;
        int col = myPosition.getColumn()-1;

        boolean notBlocked = true;
        // checking left
        for (int j = row; notBlocked && j >= 0; j--) {
            ChessPosition pos = new ChessPosition(j+1, col+1);
            if (j == row) continue;

            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                notBlocked = false;
            }
            else if (board.getPiece(pos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                notBlocked = false;
            }
        }
        notBlocked = true;
        // checking right
        for (int j = row; notBlocked && j < 8; j++) {
            ChessPosition pos = new ChessPosition(j+1, col+1);
            if (j == row) continue;

            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                notBlocked = false;
            }
            else if (board.getPiece(pos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                notBlocked = false;
            }
        }

        notBlocked = true;
        // checking down
        for (int i = col; i >= 0 && notBlocked; i--) {
            ChessPosition pos = new ChessPosition(row+1, i+1);
            if (i == col) continue;

            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                notBlocked = false;
            }
            else if (board.getPiece(pos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                notBlocked = false;
            }
        }
        notBlocked = true;
        // checking up
        for (int i = col; i < 8 && notBlocked; i++) {
            ChessPosition pos = new ChessPosition(row+1, i+1);
            if (i == col) continue;

            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                notBlocked = false;
            }
            else if (board.getPiece(pos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                notBlocked = false;
            }
        }
        return moves;
    }
}

class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow()-1;
        int col = myPosition.getColumn()-1;

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) return getWhiteMoves(board, myPosition, moves, row, col);
        return getBlackMoves(board, myPosition, moves, row, col);
    }

    private Collection<ChessMove> getWhiteMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col) {
        //if front not blocked
        if (row < 8) {
            // if can capture (piece of other team diagonally left or right)
            if (col - 1 >= 0) {
                ChessPosition enemyLeft = new ChessPosition(row + 2, col);
                if (board.getPiece(enemyLeft) != null && board.getPiece(enemyLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, enemyLeft, ChessPiece.PieceType.QUEEN));
                }
            }
            if (col + 1 < 8) {
                ChessPosition enemyRight = new ChessPosition(row + 2, col + 2);
                if (board.getPiece(enemyRight) != null && board.getPiece(enemyRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, enemyRight, null));
                }
            }
            // if front clear, add front
            // promote??
            if (board.getPiece(new ChessPosition(row+2, col+1)) == null) {
                ChessPosition posFront = new ChessPosition(row+2, col+1);
                moves.add(new ChessMove(myPosition, posFront, null));
                // inside ^, if front's front also clear and pawn in second row, add that
                if (board.getPiece(new ChessPosition(row + 3, col + 1)) == null && row == 1) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 3, col + 1), null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getBlackMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col) {
        //if front not blocked
        if (row < 8) {
            // if can capture (piece of other team diagonally left or right)
            if (col - 1 >= 0) {
                ChessPosition enemyLeft = new ChessPosition(row, col);
                if (board.getPiece(enemyLeft) != null && board.getPiece(enemyLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, enemyLeft, null));
                }
            }
            if (col + 1 < 8) {
                ChessPosition enemyRight = new ChessPosition(row, col + 2);
                if (board.getPiece(enemyRight) != null && board.getPiece(enemyRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, enemyRight, null));
                }
            }
            // if front clear, add front
            // promote??
            if (board.getPiece(new ChessPosition(row, col+1)) == null) {
                ChessPosition posFront = new ChessPosition(row, col+1);
                moves.add(new ChessMove(myPosition, posFront, null));
                // inside ^, if front's front also clear and pawn in second row, add that
                if (board.getPiece(new ChessPosition(row -1, col + 1)) == null && row == 6) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 1), null));
                }
            }
        }
        return moves;
    }
    private void getPromotionType(ChessBoard board, int row, ChessPosition before, ChessPosition after, Collection<ChessMove> moves) {
        if (row == 0 || row == 7) {
            List <ChessPiece.PieceType> list = new ArrayList<>();
            list.add(ChessPiece.PieceType.QUEEN);
            list.add(ChessPiece.PieceType.ROOK);
            list.add(ChessPiece.PieceType.BISHOP);
            list.add(ChessPiece.PieceType.KNIGHT);
//            moves.add(new ChessMove(before, after, list));
        }
        else moves.add(new ChessMove(before, after, null));
    }
}
