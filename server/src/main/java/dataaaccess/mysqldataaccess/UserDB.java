package dataaaccess.mysqldataaccess;

import dataaaccess.DataAccessException;
import dataaaccess.UserDAO;

public class UserDB implements UserDAO {

    public UserDB() {
        configureDatabase();

    }

    private void configureDatabase() {

    }

    public void clearUsers() throws DataAccessException {

    }
}
