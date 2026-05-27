package dataaaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    void clearAuth() throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    boolean findAuth(String auth) throws DataAccessException;

    ArrayList<AuthData> list() throws DataAccessException;

    void deleteAuth(String auth) throws DataAccessException;
}
