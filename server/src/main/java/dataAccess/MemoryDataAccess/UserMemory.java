package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import java.util.ArrayList;
import model.UserData;
import service.Request.*;


public class UserMemory implements UserDAO {
    final ArrayList<UserData> users = new ArrayList<>();

    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    public boolean getUser(UserData u) throws DataAccessException {
        //finds user by username
        return users.stream().anyMatch(UserData -> UserData.username().equals(u.username()));
    }

    public boolean findUser(UserData u) {
        //finds user by username && password
        return users.stream().anyMatch(UserData -> UserData.username().equals(u.username()) && UserData.password().equals(u.password()));
    }

    public void createUser(UserData userData) throws DataAccessException {
        users.add(userData);
    }

    public ArrayList<UserData> listUsers() {
        return users;
    }
}
