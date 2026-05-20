package service;

import dataAccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Request.RegisterRequest;
import service.Result.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {

    public UserService userService = new UserService();
    public ClearService clearService = new ClearService();

    @BeforeEach
    void clear() throws DataAccessException {
        clearService.clearAll();
    }

    @Test
    void clearAll() throws DataAccessException {
        RegisterResult r = userService.register(new RegisterRequest("Bob", "b123", "b@email"));
        clearService.clearAll();
        ArrayList<UserData> users = userService.list();
        assertTrue(users.isEmpty());
    }

    @Test
    void register() throws DataAccessException {
        RegisterRequest userData = new RegisterRequest("Bob", "b123", "b@email");
        UserData u = new UserData(userData.username(), userData.password(), userData.email());
        RegisterResult register = userService.register(userData);

        ArrayList<UserData> users = userService.list();
        assertTrue(users.contains(u));
        UserData ud = new UserData("Bob", "b123", "b@email");

        assertThrows(AlreadyTakenException.class, () -> {
            RegisterResult r = userService.register(new RegisterRequest("Bob", "b123", "b@email"));
        });
    }

}
