package server;

import dataAccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear);

        // Register your endpoints and exception handlers here.

    }

    private void clear(Context context) throws DataAccessException {
        new ClearService().clearAll();
        context.status(200);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
