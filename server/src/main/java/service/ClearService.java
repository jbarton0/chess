package service;

import DataAccess.DataAccessException;
import server.Server;

public class ClearService {
    public void clearAll() throws DataAccessException {
        clearAllUsers();
        clearAllAuth();
        clearAllGames();
    }

    private void clearAllUsers() throws DataAccessException {
        Server.USER_MEMORY.clearUsers();
    }

    private void clearAllAuth() throws DataAccessException {
        Server.AUTH_MEMORY.clearAuth();
    }

    private void clearAllGames() throws DataAccessException {
        Server.GAME_MEMORY.clearGames();
    }
}
