package service;

import DataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.*;
import service.result.*;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {

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
        assertTrue(users.contains(u));
        UserData ud = new UserData("Bob", "b123", "b@email");

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
        assertFalse(authTokens.contains(data));

        LoginResult loginResult = userService.login(new LoginRequest(register.username(), "b123"));
        assertTrue(authTokens.contains(new AuthData(loginResult.authToken(), loginResult.username())));

        assertThrows(IncorrectLoginException.class, () -> {
            LoginResult result = userService.login(new LoginRequest("Joe", "joeMomma"));
        });
    }

    @Test
    void logout() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        ArrayList<AuthData> authTokens = userService.listAuth();
        AuthData data = new AuthData(register.authToken(), register.username());
        assertTrue(authTokens.contains(data));

        userService.logout(new LogoutRequest(register.authToken()));
        assertFalse(authTokens.contains(data));

        assertThrows(NoAuthException.class, () -> {
            userService.logout(new LogoutRequest("abc"));
        });
    }

    @Test
    void list() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));
        assertTrue(gameService.listGames(new ListRequest(register.authToken())).games().isEmpty());

        gameService.create(new CreateRequest(register.authToken(), "gameName"));
        ListResult listResult = gameService.listGames(new ListRequest(register.authToken()));
        assertTrue(listResult.games().stream().anyMatch(gameData -> gameData.gameName().equals("gameName")));

        assertThrows(NoAuthException.class, () -> {
            gameService.create(new CreateRequest("authToken", "gameName"));
        });
    }

    @Test
    void create() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        gameService.create(new CreateRequest(register.authToken(), "gameName"));
        gameService.create(new CreateRequest(register.authToken(), "game2"));
        gameService.create(new CreateRequest(register.authToken(), "game3"));
        ListResult listResult = gameService.listGames(new ListRequest(register.authToken()));
        assertTrue(listResult.games().size() == 3);
        assertTrue(listResult.games().stream().anyMatch(gameData -> gameData.gameName().equals("gameName")));

        assertThrows(NoAuthException.class, () -> {
            gameService.create(new CreateRequest("authToken", "gameName"));
        });
    }

    @Test
    void join() throws DataAccessException {
        RegisterResult register = userService.register(new RegisterRequest("Bob", "b123", "b@email"));

        CreateResult result = gameService.create(new CreateRequest(register.authToken(), "gameName"));
        gameService.join(new JoinRequest(register.authToken(), "WHITE", result.gameID()));

        assertThrows(AlreadyTakenException.class, () -> {
            gameService.join(new JoinRequest(register.authToken(), "WHITE", result.gameID()));
        });
    }

}
