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

    @BeforeEach
    void clear() throws DataAccessException {
        new ClearService().clearAll();
    }

    @Test
    void register() throws DataAccessException {
        RegisterRequest userData = new RegisterRequest("Bob", "b123", "b@email");
        UserData u = new UserData(userData.username(), userData.password(), userData.email());
        UserService service = new UserService();
        RegisterResult register = service.register(userData);

        ArrayList<UserData> users = service.list();
        assertTrue(users.contains(u));
        UserData ud = new UserData("Bob", "b123", "b@email");

        assertThrows(AlreadyTakenException.class, () -> {
            RegisterResult r = service.register(new RegisterRequest("Bob", "b123", "b@email"));
        });
    }

}
