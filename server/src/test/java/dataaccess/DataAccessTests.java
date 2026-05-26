package dataaccess;

import dataaaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.*;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataAccessTests {

    public UserService userService = new UserService();
    public ClearService clearService = new ClearService();
    public GameService gameService = new GameService();

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
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData u = new UserData(userData.username(), hashedPassword, userData.email());
        RegisterResult register = userService.register(userData);

        ArrayList<UserData> users = userService.list();

        assertTrue(users.stream().anyMatch(user -> user.username().equals(u.username())));
    }

    @Test
    void registerNegative() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        assertThrows(AlreadyTakenException.class, () -> {
            RegisterResult r = userService.register(new RegisterRequest("Bob", "b123", "b@email"));
        });
    }
}
