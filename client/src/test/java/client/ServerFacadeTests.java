package client;

import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.*;
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
    public void registerNeg() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest(null, "bob", "bob"));
        });
    }

}
