package dataaccess;

import dataaaccess.DataAccessException;
import dataaaccess.mysqldataaccess.UserDB;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.*;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.NoAuthException;
import service.result.RegisterResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        UserData u = new UserData(userData.username(), userData.password(), userData.email());
        RegisterResult register = userService.register(userData);

        ArrayList<UserData> users = userService.list();

        assertTrue(new UserDB().findUser(u));
    }

    @Test
    void registerNegative() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        assertThrows(AlreadyTakenException.class, () -> {
            RegisterResult r = userService.register(new RegisterRequest("Bob", "b123", "b@email"));
        });
    }

    @Test
    void login() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        ArrayList<AuthData> authTokens = userService.listAuth();
        AuthData data = new AuthData(register.authToken(), register.username());
        assertTrue(authTokens.contains(data));
        userService.logout(new LogoutRequest(register.authToken()));
        assertFalse(userService.listAuth().contains(data));

        LoginResult loginResult = userService.login(new LoginRequest(register.username(), "b123"));
        assertTrue(userService.listAuth().contains(new AuthData(loginResult.authToken(), loginResult.username())));
    }

    @Test
    void loginNegative() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        assertThrows(IncorrectLoginException.class, () -> {
            LoginResult result = userService.login(new LoginRequest("Joe", "joeMomma"));
        });
    }

    @Test
    void logout() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        AuthData data = new AuthData(register.authToken(), register.username());
        assertTrue(userService.listAuth().contains(data));

        userService.logout(new LogoutRequest(register.authToken()));
        assertFalse(userService.listAuth().contains(data));
    }

    @Test
    void logoutNegative() throws DataAccessException {
        assertThrows(NoAuthException.class, () -> {
            userService.logout(new LogoutRequest("abc"));
        });
    }
}
