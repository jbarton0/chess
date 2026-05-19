package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import java.util.ArrayList;
import model.UserData;


public class UserMemory implements UserDAO {
    final ArrayList<UserData> users = new ArrayList<>();

    public void clearUsers() throws DataAccessException {
        users.clear();

    }
}
