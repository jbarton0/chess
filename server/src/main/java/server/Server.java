package server;

import com.google.gson.Gson;
import dataaaccess.DataAccessException;
import dataaaccess.memorydataaccess.*;
import dataaaccess.mysqldataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import request.*;
import server.websocket.WebSocketHandler;
import service.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.IncorrectLoginException;
import exception.NoGameException;
import service.result.*;
import dataaaccess.DatabaseManager;

public class Server {

    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;
    public final static UserDB USER_MEMORY = new UserDB();
    public final static AuthDB AUTH_MEMORY = new AuthDB();
    public final static GameDB GAME_MEMORY = new GameDB();

    public Server() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::join)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
    }

    private void clear(Context context) {
        try {
            new ClearService().clearAll();
            context.status(200);
        } catch (DataAccessException e) {
            context.result(new Gson().toJson(new Message("Error: failed to clear")));
            context.status(500);
        }
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
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);

        } catch (BadRequestException e) {
            context.result(new Gson().toJson(new Message("Error: bad request")));
            context.status(400);
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
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);

        } catch (BadRequestException e) {
            context.result(new Gson().toJson(new Message("Error: bad request")));
            context.status(400);
        }
    }

    private void logout(Context context) {
        String authToken = new Gson().fromJson(context.header("authorization"), String.class);
        LogoutRequest request = new LogoutRequest(authToken);

        try {
            new UserService().logout(request);
            context.status(200);

        } catch (NoAuthException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (DataAccessException e) {
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);
        }
    }

    private void listGames(Context context) {
        String authToken = context.header("authorization");
        ListRequest request = new ListRequest(authToken);

        try {
            ListResult result = new GameService().listGames(request);
            context.result(new Gson().toJson(result));
            context.status(200);

        } catch (NoAuthException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (DataAccessException e) {
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);
        }
    }

    private void createGame(Context context) {
        String authToken = context.header("authorization");
        CreateRequest req = new Gson().fromJson(context.body(), CreateRequest.class);
        CreateRequest request = new CreateRequest(authToken, req.gameName());

        try {
            CreateResult result = new GameService().create(request);
            context.result(new Gson().toJson(result));
            context.status(200);

        } catch (NoAuthException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (DataAccessException e) {
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);

        } catch (BadRequestException e) {
            context.result(new Gson().toJson(new Message("Error: bad request")));
            context.status(400);
        }
    }

    private void join(Context context) {
//        JoinRequest request = new Gson().fromJson(context.header("authorization"), JoinRequest.class);
        String authToken = context.header("authorization");
        JoinRequest req = new Gson().fromJson(context.body(), JoinRequest.class);
        JoinRequest request = new JoinRequest(authToken, req.playerColor(), req.gameID());

        try {
            new GameService().join(new JoinRequest(request.auth(), request.playerColor(), request.gameID()));
            context.status(200);

        } catch (NoAuthException e) {
            context.result(new Gson().toJson(new Message("Error: unauthorized")));
            context.status(401);

        } catch (AlreadyTakenException e) {
            context.result(new Gson().toJson(new Message("Error: already taken")));
            context.status(403);

        } catch (NoGameException e) {
            context.result(new Gson().toJson(new Message("Error: bad request")));
            context.status(400);

        } catch (DataAccessException e) {
            context.result(new Gson().toJson(new Message("Error: failed to access database")));
            context.status(500);

        } catch (BadRequestException e) {
            context.result(new Gson().toJson(new Message("Error: bad request")));
            context.status(400);
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
