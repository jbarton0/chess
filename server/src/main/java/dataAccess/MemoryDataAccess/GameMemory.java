package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import model.GameData;
import dataAccess.GameDAO;
import java.util.ArrayList;
import java.util.Collection;

public class GameMemory implements GameDAO {
    final ArrayList<GameData> games = new ArrayList<>();

    public void clearGames() throws DataAccessException {
        games.clear();
    }

    public Collection<GameData> list() throws DataAccessException {
        return games;
    }

    public int create(GameData gameData) {
        games.add(gameData);
        return gameData.gameID();
    }
}
