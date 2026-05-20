package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.*;
import server.Server;
import service.UserService.*;

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
        new AuthMemory().clearAuth();
    }

    private void clearAllGames() throws DataAccessException {
        new GameMemory().clearGames();
    }
}
