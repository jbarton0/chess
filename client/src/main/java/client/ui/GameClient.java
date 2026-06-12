package client.ui;

import chess.*;
import client.DrawBoard;
import client.ServerFacade;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GameClient implements NotificationHandler {
    ServerFacade facade;
    WebSocketFacade ws;
    public static GameData mostRecent;

    public GameClient(String url) {
        facade = new ServerFacade(url);
        ws = new WebSocketFacade(url, this);
    }

    public String help() {
        return """
                Actions:
                help -- possible actions
                redraw -- redraw chess board
                leave -- exit current game
                move <starting position> <ending position> -- make a new move
                resign -- forfeit current game
                highlight <position> -- highlights all possible moves for a piece
                """;
    }

    public void notify(ServerMessage message) {
        String txt = null;
        GameData game;
        if (message instanceof NotificationMessage) {
            txt = ((NotificationMessage) message).getMessage();
        } else if (message instanceof ErrorMessage) {
            txt = ((ErrorMessage) message).getErrorMessage();
        } else if (message instanceof LoadGameMessage) {
            game = ((LoadGameMessage) message).getGame();
            mostRecent = game;
            printBoard(Repl.chosenColor, game);
            txt = "";
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + txt);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resignConfirm();
                case "highlight" -> highlight(params);
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // to observe, use this function and pass in "WHITE""
    public void printBoard(String color, GameData game) {
        new DrawBoard().drawIt(color, game);
    }

    private String redraw() {
        printBoard(Repl.chosenColor, mostRecent);
        return "Board redrawn.";
    }

    private String leave() {
        try {
            ws.leave(PreLoginClient.auth, Repl.id);
            Repl.joinedGame = false;
            Repl.id = null;
            Repl.chosenColor = null;
            return "Left the game.";

        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String move(String... params) throws Exception {
        String start = params[0];
        String end = params[1];
//        String promotion = params[2];
        ChessMove move = createMove(start, end);

        try {
            ws.makeMove(PreLoginClient.auth, Repl.id, move);
            return "";
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private ChessMove createMove(String start, String end) throws ResponseException {
        List<Character> cols = new ArrayList<>(List.of('a','b','c','d','e','f','g','h'));
        ChessPosition startPos = null;
        ChessPosition endPos = null;
        Character s = start.charAt(0);
        Character e = end.charAt(0);
        Integer s2 = start.charAt(1)-'0';
        Integer e2 = end.charAt(1)-'0';

        for (int i=1; i<=8; i++) {
            if (s.equals(cols.get(i-1))) {
                startPos = new ChessPosition(s2, i);
            }
            if (e.equals(cols.get(i-1))) {
                endPos = new ChessPosition(e2, i);
            }
        }
        if (!cols.contains(s) | !cols.contains(e) | s2<1 | s2>8 | e2<1 | e2>8) {
            throw new ResponseException("Error: invalid move");
        }
        if (startPos != null) {
            if (mostRecent.game().getBoard().getPiece(startPos) == null) {
                throw new ResponseException("Error: no piece in that position");
            }
        }
        return new ChessMove(startPos, endPos, null);
    }

    private ChessPiece.PieceType findPromotion(String piece) throws ResponseException {
        if (piece.equalsIgnoreCase("queen")) {
            return ChessPiece.PieceType.QUEEN;
        } else if (piece.equalsIgnoreCase("bishop")) {
            return ChessPiece.PieceType.BISHOP;
        } else if (piece.equalsIgnoreCase("knight")) {
            return ChessPiece.PieceType.KNIGHT;
        } else if (piece.equalsIgnoreCase("rook")) {
            return ChessPiece.PieceType.ROOK;
        } else if (piece.equalsIgnoreCase("null")) {
            return null;
        } else {
            throw new ResponseException("Error: invalid promotion piece");
        }
    }

    private String resignConfirm() {
        return "resignConfirm";
    }

    public String resign(String input) throws ResponseException {
        if (input.equalsIgnoreCase("yes")) {
            ws.resign(PreLoginClient.auth, Repl.id);
            return "Successfully resigned.";
        } else {
            return "Did not resign.";
        }
    }

    private String highlight(String... params) throws Exception {
        ChessPosition pos = findPos(params[0]);
        if (pos != null) {
            if (mostRecent.game().getBoard().getPiece(pos) == null) {
                throw new ResponseException("Error: no piece in that position");
            }
        }
        Collection<ChessMove> moves = validMoves(pos);

        Collection<String> stringMoves = makeStringPos(moves);
        String chosen = pos.getRow() + "," + pos.getColumn();
        new DrawBoard().drawHighlight(Repl.chosenColor, mostRecent, stringMoves, chosen);
        return "Valid moves highlighted.";
    }

    private Collection<String> makeStringPos(Collection<ChessMove> moves) {
        Collection<String> stringMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessPosition endPos = move.getEndPosition();
            String str = endPos.getRow() + "," + endPos.getColumn();
            stringMoves.add(str);
        }
        return stringMoves;
    }

    private ChessPosition findPos(String spot) throws Exception {
        List<Character> cols = new ArrayList<>(List.of('a','b','c','d','e','f','g','h'));
        Character s1 = spot.charAt(0);
        Integer s2 = spot.charAt(1)-'0';
        ChessPosition pos = null;

        for (int i=1; i<=8; i++) {
            if (s1.equals(cols.get(i - 1))) {
                pos = new ChessPosition(s2, i);
            }
        }

        if (!cols.contains(s1) | s2<1 | s2>8) {
            throw new ResponseException("Error: invalid move");
        }

        return pos;
    }

    private Collection<ChessMove> validMoves(ChessPosition pos) {
        ChessBoard board = mostRecent.game().getBoard();
        Collection<ChessMove> possibleMoves = board.getPiece(pos).pieceMoves(board, pos);
        Collection<ChessMove> valid = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            if (isMoveValid(move, pos, possibleMoves)) {
                valid.add(move);
            }
        }
        return valid;
    }

    private boolean isMoveValid(ChessMove move, ChessPosition pos, Collection<ChessMove> possibleMoves) {
        ChessBoard board = mostRecent.game().getBoard();
        ChessBoard cloned = (ChessBoard) board.clone();
        cloned.addPiece(move.getEndPosition(), board.getPiece(pos));
        cloned.addPiece(pos, null);
        if (!possibleMoves.contains(move) | new ChessGame().isInCheckHelper(board.getPiece(pos).getTeamColor(), cloned)) {
            return false;
        }
        return true;
    }
}
