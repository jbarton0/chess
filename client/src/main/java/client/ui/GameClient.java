package client.ui;

import client.DrawBoard;
import client.ServerFacade;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.Arrays;

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
                move <starting position> <ending position> -- make a new move
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
//                case "leave" -> leave();
//                case "move" -> move(params);
//                case "resign" -> resign(params);
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

    public void leave() {
        Repl.joinedGame = false;
    }
}
