package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess.Message;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.*;
import service.Request.*;
import service.Result.*;
import service.UserService.*;
import dataAccess.MemoryDataAccess.*;

public class Server {

    private final Javalin javalin;
    public final static UserMemory userMemory = new UserMemory();
    public final static AuthMemory authMemory = new AuthMemory();
    public final static GameMemory gameMemory = new GameMemory();


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/session", this::logout)
                .post("/session", this::login)
                .delete("/db", this::clear)
                .post("/user", this::register);


        // Register your endpoints and exception handlers here.

    }

    private void clear(Context context) throws DataAccessException {
        new ClearService().clearAll();
        context.status(200);
    }

    private void register(Context context) {
        UserData user = new Gson().fromJson(context.body(), UserData.class);
        RegisterRequest req = new RegisterRequest(user.username(), user.password(), user.email());

        try {
            RegisterResult res = new UserService().register(req);
            context.result(new Gson().toJson(res));
            context.status(200);

        } catch (AlreadyTakenException e) {
            context.result(new Gson().toJson(new Message("Error: username already taken")));
            context.status(403);

        } catch (DataAccessException e) {
            context.status(500);
        }
    }

    private void login(Context context) {
        UserData user = new Gson().fromJson(context.body(), UserData.class);
        LoginRequest req = new LoginRequest(user.username(), user.password());

        try {
            LoginResult res = new UserService().login(req);
            context.result(new Gson().toJson(res));
            context.status(200);

        } catch (IncorrectLoginException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (DataAccessException e) {
            context.status(500);
        }
    }

    private void logout(Context context) {
        String authToken = new Gson(). fromJson(context.body(), String.class);
        LogoutRequest request = new LogoutRequest(authToken);

        try {
            new UserService().logout(request);
            context.status(200);

        } catch (NoAuthException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (DataAccessException e) {
            context.status(500);
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
