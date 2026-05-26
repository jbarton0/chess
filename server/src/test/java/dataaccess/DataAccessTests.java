package dataaccess;

import dataaaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import java.util.ArrayList;

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
}
