package dataaaccess.mysqldataaccess;

import dataaaccess.DataAccessException;
import dataaaccess.GameDAO;


public class GameDB implements GameDAO {

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        new UserDB().executeUpdate(statement);
    }
}
