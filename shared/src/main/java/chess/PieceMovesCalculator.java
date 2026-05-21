package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

interface PieceMovesCalculator {
    Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition);
}

class KingMovesCalculator implements PieceMovesCalculator {
    //<editor-fold>
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    //</editor-fold>

    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow()-1;
        int col = myPosition.getColumn()-1;

        for (int i = col-1; i < col+2; i++) {
            for (int j = row-1; j < row+2; j++) {
                if (row == j && col == i) {
                    continue;
                }
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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // check up
        oneDirection(myPosition, board, moves, row+2, col-1, row+2, col+1);

        //check down
        oneDirection(myPosition, board, moves, row-2, col-1, row-2, col+1);

        //check left
        oneDirection(myPosition, board, moves, row+1, col-2, row-1, col-2);

        // check right
        oneDirection(myPosition, board, moves, row+1, col+2, row-1, col+2);

        return moves;
    }

    private void oneDirection(ChessPosition startPos, ChessBoard board, Collection<ChessMove> moves, int rowUp, int colUp, int rowDown, int colDown) {
        if (rowUp > 0 && rowUp <= 8 && colUp > 0 && colUp <= 8) {
            helper(startPos, new ChessPosition(rowUp, colUp), board, moves);
        } if (rowDown > 0 && rowDown <= 8 && colDown > 0 && colDown <= 8) {
            helper(startPos, new ChessPosition(rowDown, colDown), board, moves);
        }
    }

    private void helper(ChessPosition startPos, ChessPosition endPos, ChessBoard board, Collection<ChessMove> moves) {
        if (board.getPiece(endPos) == null || board.getPiece(endPos).getTeamColor() != board.getPiece(startPos).getTeamColor()) {
            moves.add(new ChessMove(startPos, endPos, null));
        }
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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // checks left and down
        boolean notBlocked = true;
        for (int j = row, i = col; notBlocked && j > 0 && i > 0; j--, i--) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }

        // checks right and down
        notBlocked = true;
        for (int j = row, i = col; j>0 && i <= 8 && notBlocked; j--, i++) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }

        // checks left and up
        notBlocked = true;
        for (int j = row, i = col; notBlocked && j <= 8 && i>0; j++, i--) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }

        //checks right and up
        notBlocked = true;
        for (int j = row, i = col; j <= 8 && i <= 8 && notBlocked; j++, i++) {
            notBlocked = check(board, myPosition, moves, j, i, row, col);
        }
        return moves;
    }

    private boolean check(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int j, int i, int row, int col) {
        ChessPosition pos = new ChessPosition(j, i);
        if (j != row || i != col) {
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
            else if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, pos, null));
                return false;
            }
            else {
                return false;
            }
        }
        return true;
    }
}

class RookMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //check up
        boolean notBlocked = true;
        for (int i=row; notBlocked && i>0 && i<=8; i++) {
            if (i==row) {
                continue;
            }
            ChessPosition pos = new ChessPosition(i, col);
            notBlocked = check(board, moves, myPosition, pos);
        }

        //check down
        notBlocked = true;
        for (int i=row; notBlocked && i>0 && i<=8; i--) {
            if (i==row) {
                continue;
            }
            ChessPosition pos = new ChessPosition(i, col);
            notBlocked = check(board, moves, myPosition, pos);
        }

        //check right
        notBlocked = true;
        for (int i=col; notBlocked && i>0 && i<=8; i++) {
            if (i==col) {
                continue;
            }
            ChessPosition pos = new ChessPosition(row, i);
            notBlocked = check(board, moves, myPosition, pos);
        }

        //check left
        notBlocked = true;
        for (int i=col; notBlocked && i>0 && i<=8; i--) {
            if (i==col) {
                continue;
            }
            ChessPosition pos = new ChessPosition(row, i);
            notBlocked = check(board, moves, myPosition, pos);
        }

        return moves;
    }

    private boolean check(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessPosition pos) {
        if (board.getPiece(pos) == null) {
            moves.add(new ChessMove(myPosition, pos, null));
        }
        else if (board.getPiece(pos).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
            return false;
        }
        else {
            moves.add(new ChessMove(myPosition, pos, null));
            return false;
        }
        return true;
    }
}

class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            check(board, moves, myPosition, col, row+1, col-1, col+1);
            ChessPosition pos = new ChessPosition(4, col);
            if (row==2 && board.getPiece(pos)==null && board.getPiece(new ChessPosition(3, col))==null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
        }
        else {
            check(board, moves, myPosition, col, row-1, col+1, col-1);
            ChessPosition pos = new ChessPosition(5, col);
            if (row==7 && board.getPiece(pos)==null && board.getPiece(new ChessPosition(6, col))==null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
        }
        return moves;
    }

    private void check(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, int col, int front, int frontLeft, int frontRight) {
        if (front>0 && front<=8) {
            ChessPosition pos = new ChessPosition(front, col);
            if (board.getPiece(pos) == null && front==1 || front==8) {
                addPromotions(myPosition, pos, moves);
            }
            else if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }

            if (frontLeft>0 && frontLeft<=8) {
                ChessPosition pos2 = new ChessPosition(front, frontLeft);
                if (board.getPiece(pos2) != null) {
                    if (board.getPiece(pos2).getTeamColor() != board.getPiece(myPosition).getTeamColor() && front == 1 || front == 8) {
                        addPromotions(myPosition, pos2, moves);
                    }
                    else if (board.getPiece(pos2).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, pos2, null));
                    }
                }
            }
            if (frontRight>0 && frontRight<=8) {
                ChessPosition pos3 = new ChessPosition(front, frontRight);
                if (board.getPiece(pos3)!=null) {
                    if (board.getPiece(pos3).getTeamColor() != board.getPiece(myPosition).getTeamColor() && front == 1 || front == 8) {
                        addPromotions(myPosition, pos3, moves);
                    }
                    else if (board.getPiece(pos3).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, pos3, null));
                    }
                }
            }
        }
    }

    private void addPromotions(ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }
}
