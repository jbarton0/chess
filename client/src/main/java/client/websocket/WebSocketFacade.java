package client.websocket;

import chess.ChessMove;
import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import com.google.gson.Gson;

import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;


public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

//    ignore
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public WebSocketFacade(String url, NotificationHandler notificationHandler){
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationMessage msg1;
                    ErrorMessage msg2;
                    LoadGameMessage msg3;
                    if (new Gson().fromJson(message, ServerMessage.class).getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        msg1 = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(msg1);
                    } else if (new Gson().fromJson(message, ServerMessage.class).getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
                        msg2 = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notify(msg2);
                    } else if (new Gson().fromJson(message, ServerMessage.class).getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        msg3 = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(msg3);
                    }
                }
            });
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
}
