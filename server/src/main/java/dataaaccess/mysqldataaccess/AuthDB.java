package dataaaccess.mysqldataaccess;

import dataaaccess.AuthDAO;
import dataaaccess.DataAccessException;


public class AuthDB implements AuthDAO {

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE authTokens";
        new UserDB().executeUpdate(statement);
    }
}
