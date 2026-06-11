package client.ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.DrawBoard;
import client.ServerFacade;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClient implements NotificationHandler {
    ServerFacade facade;
    WebSocketFacade ws;

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
                move <starting position> <ending position> <promotion piece ('null' if n/a)> -- make a new move
                resign -- forfeit current game
                highlight <position> -- highlights all possible moves for a piece
                """;
    }

    public void notify(ServerMessage message) {
        var msg = new Gson().toJson(message);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + msg);
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
//                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
//                case "highlight" -> highlight(params);
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void play(String color) {
        String[] playingColor = new String[]{color};
        DrawBoard.main(playingColor);
    }

    public void observe() {
        String[] playingColor = new String[]{"WHITE"};
        DrawBoard.main(playingColor);
    }

//    private String redraw() {
//
//    }

    private String leave() {
        try {
            ws.leave(PreLoginClient.auth, Repl.id);
            Repl.joinedGame = false;
            Repl.id = null;
            return "Left the game.";

        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private String move(String... params) throws Exception {
        String start = params[0];
        String end = params[1];
        String promotion = params[2];
        ChessMove move = createMove(start, end, promotion);

        try {
            ws.makeMove(PreLoginClient.auth, Repl.id, move);
            return "";
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private ChessMove createMove(String start, String end, String promotion) throws ResponseException {
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
        return new ChessMove(startPos, endPos, findPromotion(promotion));
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

    private String resign() throws ResponseException {
        ws.resign(PreLoginClient.auth, Repl.id);
        return "Successfully resigned.";
    }
}
