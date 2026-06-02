package client;

import dataaaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void clear() throws ResponseException {
        facade.clearAll();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws ResponseException {
        AuthData authData = facade.register(new RegisterRequest("bob", "bob", "bob"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerNeg() {
        assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest(null, "bob", "bob"));
        });
    }

    @Test
    public void login() throws ResponseException {
        AuthData authData = facade.register(new RegisterRequest("bob", "bob", "bob"));
        facade.logout(new LogoutRequest(authData.authToken()));
        AuthData auth = facade.login(new LoginRequest("bob", "bob"));
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void loginNeg() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest(null, "bob"));
        });
    }

    @Test
    public void logout() throws ResponseException {
        AuthData authData = facade.register(new RegisterRequest("bob", "bob", "bob"));
        assertDoesNotThrow(() -> facade.logout(new LogoutRequest(authData.authToken())));
    }

    @Test
    public void logoutNeg() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.logout(new LogoutRequest(null));
        });
    }

}
