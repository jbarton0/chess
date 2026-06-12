package client.ui;

import client.ServerFacade;
import client.State;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.GameList;
import request.*;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class PostLoginClient {
    ServerFacade facade;
    WebSocketFacade ws;
    GameClient gameClient;

    public PostLoginClient(String url) {
        facade = new ServerFacade(url);
        gameClient = new GameClient(url);
        ws = new WebSocketFacade(url, gameClient);
    }

    public String help() {
        return """
                Actions:
                help -- possible actions
                logout -- stop playing chess
                list -- see all available games
                create <gamename> -- create a new chess game
                join <id> <WHITE|BLACK> -- start playing
                observe <id> -- watch gameplay
                quit -- say goodbye
                """;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "logout" -> logout();
                case "list" -> list();
                case "create" -> create(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        facade.logout(new LogoutRequest(PreLoginClient.auth));
        Repl.state = State.SIGNEDOUT;
        PreLoginClient.auth = null;
        return "Successfully logged out user." + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
    }

    private String list() throws ResponseException {
        assertSignedIn();
        GameList list = facade.listGames(new ListRequest(PreLoginClient.auth));
        ArrayList<GameData> games = list.getGames();
        StringBuilder newString = new StringBuilder();
        int counter = 1;
        if (games.isEmpty()) { return newString.append("There are 0 games available.").toString(); }
        for (var game : games) {
            newString.append(counter).append(". ").append(game.gameName()).append(" - white player: ")
                    .append(game.whiteUsername()).append(", black player: ").append(game.blackUsername()).append("\n");
            counter += 1;
        }
        return newString.toString();
    }

    private String create(String... params) throws ResponseException {
        if (params.length == 1) {
            GameData gameData = facade.create(new CreateRequest(PreLoginClient.auth, params[0]));
            return "Successfully created game " + params[0] + ".";
        }
        throw new ResponseException("Error: incorrect number of inputs");
    }

    private String join(String... params) throws ResponseException {
        if (params.length == 2) {
            String upperColor = params[1].toUpperCase();
            boolean validColor = upperColor.equals("WHITE") | upperColor.equals("BLACK");
            if (validColor) {
                Repl.chosenColor = upperColor;
                ArrayList<GameData> games = facade.listGames(new ListRequest(PreLoginClient.auth)).getGames();
                int fakeID = Integer.parseInt(params[0]);
                if (fakeID <= games.size()) {
                    var gameID = games.get(fakeID - 1).gameID();
                    Repl.id = gameID;
                    facade.join(new JoinRequest(PreLoginClient.auth, upperColor, gameID));
                    Repl.joinedGame = true;
                    ws.connect(PreLoginClient.auth, gameID);
//                    gameClient.printBoard(upperColor, games.get(fakeID-1));
                    return "Successfully joined game.";
                }
                throw new ResponseException("Error: invalid game ID");
            }
            throw new ResponseException("Error: invalid player color");
        }
        throw new ResponseException("Error: incorrect number of inputs");
    }

    private String observe(String... params) throws ResponseException {
        ArrayList<GameData> games = facade.listGames(new ListRequest(PreLoginClient.auth)).getGames();
        int fakeID = Integer.parseInt(params[0]);
        if (fakeID <= games.size()) {
            Repl.id = games.get(fakeID - 1).gameID();
            gameClient.printBoard("WHITE", games.get(Repl.id));
            ws.connect(PreLoginClient.auth, Repl.id);
            Repl.joinedGame = true;
            return "Observing game " + params[0];
        }
        throw new ResponseException("Error: invalid game ID");
    }

    private void assertSignedIn() throws ResponseException {
        if (Repl.state == State.SIGNEDOUT) {
            throw new ResponseException("You must sign in");
        }
    }
}
