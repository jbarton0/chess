package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
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
        if (connections.get(gameID).isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (List<Session> lst : connections.values()) {
            for (Session c : lst) {
                if (c.isOpen()) {
                    if (!c.equals(excludeSession)) {
                        c.getRemote().sendString(msg);
                    }
                }
            }
        }
    }
}
