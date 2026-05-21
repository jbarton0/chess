package DataAccess;

public abstract interface UserDAO {
    // configure so that methods take as an argument the location where things will be stored
    // aka mySQL or internal list
    // so it's easy to switch over for phase 4

    // data access methods should throw some kind of exception
    void clearUsers() throws DataAccessException;
}

