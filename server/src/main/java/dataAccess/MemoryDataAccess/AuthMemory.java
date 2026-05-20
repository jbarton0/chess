package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import model.AuthData;
import dataAccess.AuthDAO;
import java.util.ArrayList;

public class AuthMemory implements AuthDAO {
    final ArrayList<AuthData> authTokens = new ArrayList<>();

    public void clearAuth() throws DataAccessException {
        authTokens.clear();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        authTokens.add(authData);
    }
}
