package dataaaccess;

import model.GameData;
import service.request.JoinRequest;

import java.util.Collection;

public interface GameDAO {
    void clearGames() throws DataAccessException;

    int create(GameData gameData) throws DataAccessException;

    Collection<GameData> list() throws DataAccessException;

    void join(GameData gameData, JoinRequest joinRequest) throws DataAccessException;

    boolean findGame(int id) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;
}
