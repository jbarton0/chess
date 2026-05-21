package dataaaccess.memorydataaccess;

import dataaaccess.DataAccessException;
import dataaaccess.UserDAO;
import java.util.ArrayList;
import model.UserData;


public class UserMemory implements UserDAO {
    final ArrayList<UserData> users = new ArrayList<>();

    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    public boolean getUser(UserData u) throws DataAccessException {
        //finds user by username
        return users.stream().anyMatch(userData -> userData.username().equals(u.username()));
    }

    public boolean findUser(UserData u) {
        //finds user by username && password
        return users.stream().anyMatch(userData -> userData.username().equals(u.username()) && userData.password().equals(u.password()));
    }

    public void createUser(UserData userData) throws DataAccessException {
        users.add(userData);
    }

    public ArrayList<UserData> listUsers() {
        return users;
    }
}
