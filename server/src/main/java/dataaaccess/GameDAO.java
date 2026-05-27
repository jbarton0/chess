package dataaaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    int create(GameData gameData) throws DataAccessException;

    Collection<GameData> list() throws DataAccessException;
}
