package service;

import dataAccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceTests {

    @BeforeEach
    void clear() throws DataAccessException {
        new ClearService().clearAll();
    }

    @Test
    void register() throws DataAccessException {
        UserData userData = new UserData("Bob", "b123", "b@email");

    }

}
