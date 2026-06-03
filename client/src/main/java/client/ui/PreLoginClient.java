package client.ui;

import client.ServerFacade;
import client.State;
import exception.ResponseException;
import model.AuthData;
import request.LoginRequest;
import request.RegisterRequest;
import ui.EscapeSequences;

import java.util.Arrays;

public class PreLoginClient {
    ServerFacade facade;
    public static String auth = null;

    public PreLoginClient(String url) {
        facade = new ServerFacade(url);
    }

    public String help() {
        return """
                Actions:
                help -- possible actions
                register <username> <password> <email> -- create an account
                login <username> <password> -- play chess
                quit -- say goodbye
                """;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length == 3) {
            AuthData authData = facade.register(new RegisterRequest(params[0], params[1], params[2]));
            auth = authData.authToken();
            Repl.state = State.SIGNEDIN;
            return "Successfully registered user." + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        }
        throw new ResponseException("Error: username, password, or email missing");
    }

    private String login(String... params) throws ResponseException {
        if (params.length == 2) {
            AuthData authData = facade.login(new LoginRequest(params[0], params[1]));
            auth = authData.authToken();
            Repl.state = State.SIGNEDIN;
            return "Successfully logged in user." + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        }
        throw new ResponseException("Error: username or password missing");
    }
}
