package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
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
        // get register request??
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
