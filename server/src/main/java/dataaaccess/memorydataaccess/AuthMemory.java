package dataaaccess.memorydataaccess;

import dataaaccess.DataAccessException;
import model.AuthData;
import dataaaccess.AuthDAO;
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
        return authTokens.stream().anyMatch(authData -> authData.authToken().equals(auth));
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.stream().filter(authData -> authData.authToken().equals(authToken)).findFirst().orElse(null);
    }

    public void deleteAuth(String auth) throws DataAccessException {
        AuthData authData = getAuth(auth);
        authTokens.remove(authData);
    }

    public ArrayList<AuthData> list() {
        return authTokens;
    }
}
