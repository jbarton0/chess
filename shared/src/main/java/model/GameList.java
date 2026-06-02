package model;

import java.util.ArrayList;
import java.util.Collection;

public class GameList extends ArrayList<GameData> {
    public GameList() { }

    public GameList(Collection<GameData> games) {
        super(games);
    }
}
