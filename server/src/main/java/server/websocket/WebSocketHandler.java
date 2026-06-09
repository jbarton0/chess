package server.websocket;

import com.google.gson.Gson;
import dataaaccess.DataAccessException;
import dataaaccess.mysqldataaccess.GameDB;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

import dataaaccess.mysqldataaccess.AuthDB;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDB authDB = new AuthDB();
    private final GameDB gameDB = new GameDB();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
//                case MAKE_MOVE -> exit(action.visitorName(), ctx.session);
//                case LEAVE -> exit(action.visitorName(), ctx.session);
//                case RESIGN -> exit(action.visitorName(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String auth, Integer gameID, Session session) throws Exception {
        if (gameID == null | gameDB.getGame(gameID) == null) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid game ID"));
            return;
        }
        connections.add(session, gameID);
        if (!authDB.findAuth(auth)) {
            sendError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid authentication"));
            return;
        }
        AuthData authData = authDB.getAuth(auth);
        GameData gameData = gameDB.getGame(gameID);
        sendLoadGame(session, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
        sendJoined(session, gameData, authData);
    }

    private void sendLoadGame(Session session, LoadGameMessage msg) throws IOException {
        var ms = new Gson().toJson(msg);
        session.getRemote().sendString(ms);
    }

    private void sendJoined(Session session, GameData gameData, AuthData authData) throws IOException {
        String message;
        if (gameData.whiteUsername().equals(authData.username())) {
            message = String.format("%s has joined the game as WHITE", authData.username());
        } else {
            message = String.format("%s has joined the game as BLACK", authData.username());
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }

    private void sendError(Session session, ErrorMessage msg) throws Exception{
        var ms = new Gson().toJson(msg);
        session.getRemote().sendString(ms);
    }
}
