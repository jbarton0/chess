package client.ui;

import client.DrawBoard;
import client.ServerFacade;

public class GameClient {
    ServerFacade facade;

    public GameClient(ServerFacade facade) {
        this.facade = facade;
    }

    public void play(String color) {
        String[] playingColor = new String[]{color};
        DrawBoard.main(playingColor);
    }

    public void observe() {
        String[] playingColor = new String[]{"WHITE"};
        DrawBoard.main(playingColor);
    }
}
