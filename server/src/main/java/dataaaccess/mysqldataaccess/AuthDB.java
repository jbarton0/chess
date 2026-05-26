package dataaaccess.mysqldataaccess;

import dataaaccess.AuthDAO;
import dataaaccess.DataAccessException;
import model.AuthData;


public class AuthDB implements AuthDAO {

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE authTokens";
        new UserDB().executeUpdate(statement);
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?)";
        new UserDB().executeUpdate(statement, authData.authToken(), authData.username());
    }
}
