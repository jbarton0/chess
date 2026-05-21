package service;

import dataAccess.DataAccessException;
import server.Server;

public class ClearService {
    public void clearAll() throws DataAccessException {
        clearAllUsers();
        clearAllAuth();
        clearAllGames();
    }

    private void clearAllUsers() throws DataAccessException {
        Server.userMemory.clearUsers();
    }

    private void clearAllAuth() throws DataAccessException {
        Server.authMemory.clearAuth();
    }

    private void clearAllGames() throws DataAccessException {
        Server.gameMemory.clearGames();
    }
}
