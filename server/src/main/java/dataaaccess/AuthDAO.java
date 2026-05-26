package dataaaccess;

import model.AuthData;

public interface AuthDAO {
    void clearAuth() throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;
}
