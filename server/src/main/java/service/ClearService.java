package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.*;

public class ClearService {
    public void clearAll() throws DataAccessException {
        clearAllUsers();
        clearAllAuth();
        clearAllGames();
    }

    private void clearAllUsers() throws DataAccessException {
        new UserMemory().clearUsers();
    }

    private void clearAllAuth() throws DataAccessException {
        new AuthMemory().clearAuth();
    }

    private void clearAllGames() throws DataAccessException {
        new GameMemory().clearGames();
    }
}
