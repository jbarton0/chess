package dataaaccess;

import model.UserData;

import java.util.ArrayList;

public abstract interface UserDAO {
    // configure so that methods take as an argument the location where things will be stored
    // aka mySQL or internal list
    // so it's easy to switch over for phase 4

    // data access methods should throw some kind of exception
    void clearUsers() throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    boolean getUser(UserData u) throws DataAccessException;

    boolean findUser(UserData u) throws DataAccessException;

    ArrayList<UserData> listUsers() throws DataAccessException;
}

