package dataaccess;

import chess.ChessGame;
import dataaaccess.DataAccessException;
import dataaaccess.mysqldataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;
import service.request.*;
import service.result.*;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private final ClearService clearService = new ClearService();
    private final UserDB userDB = new UserDB();
    private final AuthDB authDB = new AuthDB();
    private final GameDB gameDB = new GameDB();

    @BeforeEach
    void clear() throws DataAccessException {
        clearService.clearAll();
    }

    //UserDAO tests
    @Test
    void clearUsers() throws DataAccessException {
        userDB.createUser(new UserData("Bob", "b123", "b@email"));
        userDB.clearUsers();
        ArrayList<UserData> users = userDB.listUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void createUser() throws DataAccessException {
        userDB.createUser(new UserData("Bob", "b123", "b@email"));
        ArrayList<UserData> users = userDB.listUsers();
        assertEquals(1, users.size());
    }

    @Test
    void createUserNeg() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            userDB.createUser(new UserData(null, null, null));
        });
    }

    @Test
    void getUser() throws DataAccessException {
        userDB.createUser(new UserData("Bob", "b123", "b@email"));
        assertTrue(userDB.getUser(new UserData("Bob", "b123", "b@email")));
    }

    @Test
    void getUserNeg() throws DataAccessException {
        assertFalse(userDB.getUser(new UserData("Bob", "b123", "b@email")));
    }

    @Test
    void findUser() throws DataAccessException {
        userDB.createUser(new UserData("Bob", "b123", "b@email"));
        assertTrue(userDB.findUser(new UserData("Bob", "b123", "b@email")));
    }

    @Test
    void findUserNeg() throws DataAccessException {
        assertFalse(userDB.findUser(new UserData("Bob", "b123", "b@email")));
    }

    @Test
    void listUsers() throws DataAccessException {
        userDB.createUser(new UserData("Bob", "b123", "b@email"));
        userDB.createUser(new UserData("Joe", "j123", "j@email"));
        assertEquals(2, userDB.listUsers().size());
    }

    @Test
    void listUsersNeg() throws DataAccessException {
        assertEquals(0, userDB.listUsers().size());
    }

    //AuthDAO tests
    @Test
    void clearAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        authDB.clearAuth();
        ArrayList<AuthData> auth = authDB.list();
        assertTrue(auth.isEmpty());
    }

    @Test
    void createAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        ArrayList<AuthData> auth = authDB.list();
        assertEquals(1, auth.size());
    }

    @Test
    void createAuthNeg() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            authDB.createAuth(new AuthData(null, null));
        });
    }

    @Test
    void getAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        assertNotNull(authDB.getAuth("abc"));
    }

    @Test
    void getAuthNeg() throws DataAccessException {
        assertNull(authDB.getAuth("abc"));
    }

    @Test
    void findAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        assertTrue(authDB.findAuth("abc"));
    }

    @Test
    void findAuthNeg() throws DataAccessException {
        assertFalse(authDB.findAuth("abc"));
    }

    @Test
    void listAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        authDB.createAuth(new AuthData("def", "Joe"));
        assertEquals(2, authDB.list().size());
    }

    @Test
    void listAuthNeg() throws DataAccessException {
        assertEquals(0, authDB.list().size());
    }

    @Test
    void deleteAuth() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        authDB.createAuth(new AuthData("def", "Joe"));
        authDB.deleteAuth("abc");
        assertEquals(1, authDB.list().size());
    }

    @Test
    void deleteAuthNeg() throws DataAccessException {
        authDB.deleteAuth("abc");
        assertEquals(0, authDB.list().size());
    }

    //GameDAO tests
    @Test
    void clearGames() throws DataAccessException {
        gameDB.create(new GameData(1, null, null, "gameName", new ChessGame()));
        gameDB.clearGames();
        Collection<GameData> games = gameDB.list();
        assertTrue(games.isEmpty());
    }

    @Test
    void createGame() throws DataAccessException {
        gameDB.create(new GameData(1, null, null, "gameName", new ChessGame()));
        Collection<GameData> games = gameDB.list();
        assertEquals(1, games.size());
    }

    @Test
    void createGameNeg() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            gameDB.create(new GameData(0, null, null, null, null));
        });
    }

    @Test
    void getGame() throws DataAccessException {
        gameDB.create(new GameData(1, null, null, "gameName", new ChessGame()));
        assertNotNull(gameDB.getGame(1));
    }

    @Test
    void getGameNeg() throws DataAccessException {
        assertNull(gameDB.getGame(0));
    }

    @Test
    void findGame() throws DataAccessException {
        gameDB.create(new GameData(1, null, null, "gameName", new ChessGame()));
        assertTrue(gameDB.findGame(1));
    }

    @Test
    void findGameNeg() throws DataAccessException {
        assertFalse(gameDB.findGame(1));
    }

    @Test
    void listGames() throws DataAccessException {
        gameDB.create(new GameData(1, null, null, "gameName", new ChessGame()));
        gameDB.create(new GameData(2, null, null, "gameName2", new ChessGame()));
        assertEquals(2, gameDB.list().size());
    }

    @Test
    void listGamesNeg() throws DataAccessException {
        assertEquals(0, gameDB.list().size());
    }

    @Test
    void joinGame() throws DataAccessException {
        authDB.createAuth(new AuthData("abc", "Bob"));
        var g = new GameData(1, null, null, "gameName", new ChessGame());
        gameDB.create(g);
        gameDB.join(g, new JoinRequest("abc", "BLACK", 1));
        assertEquals(1, gameDB.list().size());
    }

    @Test
    void joinGameNeg() throws DataAccessException {
        var g = new GameData(1, null, null, "gameName", new ChessGame());
        assertThrows(NullPointerException.class, () -> {
            gameDB.join(g, new JoinRequest("abc", "BLACK", 1));        });
    }
}
