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
        Collection<ChessMove> diagonalMoves = new BishopMovesCalculator().getMoves(board, myPosition);
        Collection<ChessMove> straightMoves = new RookMovesCalculator().getMoves(board, myPosition);
        diagonalMoves.addAll(straightMoves);
        return diagonalMoves;
    }
}

class KnightMovesCalculator implements PieceMovesCalculator {
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

        // check up
        if (row+2 < 8 && col-1 >= 0) {
            ChessPosition upLeft = new ChessPosition(row + 3, col);
            if (board.getPiece(upLeft) == null || board.getPiece(upLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, upLeft, null));
            }
        } if (row+2 < 8 && col+1 < 8) {
            ChessPosition upRight = new ChessPosition(row+3, col+2);
            if (board.getPiece(upRight) == null || board.getPiece(upRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, upRight, null));
            }
        }
        //check down
        if (row-2 >= 0 && col-1 >= 0) {
            ChessPosition downLeft = new ChessPosition(row - 1, col);
            if (board.getPiece(downLeft) == null || board.getPiece(downLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, downLeft, null));
            }
        } if (row-2 >= 0 && col+1 < 8) {
            ChessPosition downRight = new ChessPosition(row-1, col+2);
            if (board.getPiece(downRight) == null || board.getPiece(downRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, downRight, null));
            }
        }
        //check left
        if (col-2 >= 0 && row+1 < 8) {
            ChessPosition leftUp = new ChessPosition(row + 2, col - 1);
            if (board.getPiece(leftUp) == null || board.getPiece(leftUp).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, leftUp, null));
            }
        } if (col-2 >= 0 && row-1 >= 0) {
            ChessPosition leftDown = new ChessPosition(row, col-1);
            if (board.getPiece(leftDown) == null || board.getPiece(leftDown).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, leftDown, null));
            }
        }
        // check right
        if (col+2 < 8 && row+1 < 8) {
            ChessPosition rightUp = new ChessPosition(row + 2, col + 3);
            if (board.getPiece(rightUp) == null || board.getPiece(rightUp).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, rightUp, null));
            }
        } if (col+2 < 8 && row-1 >= 0) {
            ChessPosition rightDown = new ChessPosition(row, col+3);
            if (board.getPiece(rightDown) == null || board.getPiece(rightDown).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, rightDown, null));
            }
        }
        return moves;
    }
}

class BishopMovesCalculator implements PieceMovesCalculator {
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
        for (int j = row, i = col; notBlocked && j >= 0 && i >= 0; j--, i--) {
            // checks left and down
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }
        notBlocked = true;
        // checks right and down
        for (int j = row, i = col; j>=0 && i < 8 && notBlocked; j--, i++) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }

        notBlocked = true;
        // checks left and up
        for (int j = row, i = col; notBlocked && j < 8 && i>=0; j++, i--) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }
        notBlocked = true;
        //checking right and up
        for (int j = row, i = col; j < 8 && i < 8 && notBlocked; j++, i++) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }
        return moves;
    }

    private boolean check(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int j, int i, int row, int col) {
        ChessPosition pos = new ChessPosition(j+1, i+1);
        if (j != row || i != col) {
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                return false;
            }
            else return false;
        }
        return true;
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
        if (row+1 < 8) {
            // if can capture (piece of other team diagonally left or right)
            if (col - 1 >= 0) {
                ChessPosition enemyLeft = new ChessPosition(row + 2, col);
                if (board.getPiece(enemyLeft) != null && board.getPiece(enemyLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) getPromotionType(board, row+2, myPosition, enemyLeft, moves);
            }
            if (col + 1 < 8) {
                ChessPosition enemyRight = new ChessPosition(row + 2, col + 2);
                if (board.getPiece(enemyRight) != null && board.getPiece(enemyRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) getPromotionType(board, row+2, myPosition, enemyRight, moves);
            }
            // if front clear, add front
            if (board.getPiece(new ChessPosition(row+2, col+1)) == null) {
                ChessPosition posFront = new ChessPosition(row+2, col+1);
                getPromotionType(board, row+2, myPosition, posFront, moves);

                // inside ^, if front's front also clear and pawn in second row, add that
                if (row+2 < 8) {
                    if (board.getPiece(new ChessPosition(row + 3, col + 1)) == null && row == 1) {
                        ChessPosition posFront2 = new ChessPosition(row + 3, col + 1);
                        getPromotionType(board, row + 3, myPosition, posFront2, moves);
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> getBlackMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col) {
        //if front not blocked
        if (row-1 >= 0) {
            // if can capture (piece of other team diagonally left or right)
            if (col - 1 >= 0) {
                ChessPosition enemyLeft = new ChessPosition(row, col);
                if (board.getPiece(enemyLeft) != null && board.getPiece(enemyLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) getPromotionType(board, row, myPosition, enemyLeft, moves);
            }
            if (col + 1 < 8) {
                ChessPosition enemyRight = new ChessPosition(row, col + 2);
                if (board.getPiece(enemyRight) != null && board.getPiece(enemyRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) getPromotionType(board, row, myPosition, enemyRight, moves);
            }
            // if front clear, add front
            if (board.getPiece(new ChessPosition(row, col+1)) == null) {
                ChessPosition posFront = new ChessPosition(row, col+1);
                getPromotionType(board, row, myPosition, posFront, moves);

                // inside ^, if front's front also clear and pawn in second row, add that
                if (row-2 >= 0) {
                    if (board.getPiece(new ChessPosition(row -1, col + 1)) == null && row == 6) {
                        ChessPosition posFront2 = new ChessPosition(row-1,col+1);
                        getPromotionType(board, row-1, myPosition, posFront2, moves);
                    }
                }
            }
        }
        return moves;
    }

    private void getPromotionType(ChessBoard board, int row, ChessPosition before, ChessPosition after, Collection<ChessMove> moves) {
        if ((board.getPiece(after) == null && after.getRow() == 1) || (board.getPiece(after) == null && after.getRow() == 8)) {
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.KNIGHT));
        }
        else if (row == 1 && board.getPiece(before).getTeamColor() == ChessGame.TeamColor.BLACK || row == 8 && board.getPiece(before).getTeamColor() == ChessGame.TeamColor.WHITE) {
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(before, after, ChessPiece.PieceType.KNIGHT));
        }
        else moves.add(new ChessMove(before, after, null));
    }
}
