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

    public boolean findAuth(String auth) throws DataAccessException {
        return authTokens.stream().anyMatch(AuthData -> AuthData.authToken().equals(auth));
    }

    public void deleteAuth(String auth) {
        AuthData authData = authTokens.stream().filter(AuthData -> AuthData.authToken().equals(auth)).findFirst().orElse(null);
        authTokens.remove(authData);
    }

    public ArrayList<AuthData> list() {
        return authTokens;
    }
}
