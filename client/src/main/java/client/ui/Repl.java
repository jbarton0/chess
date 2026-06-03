package client.ui;

import java.util.Scanner;

import client.State;
import ui.EscapeSequences;

public class Repl {
    private final PreLoginClient preClient;
    private final PostLoginClient postClient;
    public static State state = State.SIGNEDOUT;

    public Repl(String url) {
        preClient = new PreLoginClient(url);
        postClient = new PostLoginClient(url);
    }

    public void run() {
        System.out.println("♕ Welcome to chess. Login to get started. ♕");

        if (state == State.SIGNEDOUT) {
            System.out.print(preClient.help());
        }
        else { System.out.print(postClient.help()); }

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            if (state == State.SIGNEDOUT) {
                try {
                    result = preClient.eval(line);
                    System.out.print(result + EscapeSequences.SET_TEXT_COLOR_GREEN);

                } catch (Exception e) {
                    System.out.print(e.toString() + EscapeSequences.SET_TEXT_COLOR_RED);
                }
            }

            else {
                try {
                    result = postClient.eval(line);
                    System.out.print(result + EscapeSequences.SET_TEXT_COLOR_GREEN);

                } catch (Exception e) {
                    System.out.print(e.toString());
                }
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n[" + state.toString() + "] >>> " + EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD);
    }
}
