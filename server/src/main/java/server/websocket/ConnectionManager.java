package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    public final Map<Integer, List<Session>> connections = new HashMap<>();

    public void add(Session session, Integer gameID) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void remove(Session session, Integer gameID) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException {
        var message = new Gson().toJson(notification);
        for (List<Session> lst : connections.values()) {
            if (connections.get(gameID).equals(lst)) {
                for (Session c : lst) {
                    if (c.isOpen()) {
                        if (!c.equals(excludeSession)) {
//                            if (message!=null) {
//                                c.getRemote().sendString(message);
//                            } else if (game!=null) {
//                                c.getRemote().sendString(new Gson().toJson(game));
//                            }
                            c.getRemote().sendString(message);
                        }
                    }
                }
            }
        }
    }
}
