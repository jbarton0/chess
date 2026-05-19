package dataAccess.MemoryDataAccess;

import dataAccess.DataAccessException;
import model.UserData;
import dataAccess.AuthDAO;
import java.util.ArrayList;

public class AuthMemory implements AuthDAO {
    final ArrayList<UserData> authTokens = new ArrayList<>();

    public void clearAuth() throws DataAccessException {
        authTokens.clear();
    }
}
