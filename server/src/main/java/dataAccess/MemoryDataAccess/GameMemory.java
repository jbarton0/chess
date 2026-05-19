package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import model.UserData;
import dataAccess.GameDAO;
import java.util.ArrayList;

public class GameMemory implements GameDAO {
    final ArrayList<UserData> games = new ArrayList<>();

    public void clearGames() throws DataAccessException {
        games.clear();
    }
}
