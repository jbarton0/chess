package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public Map<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void remove(Session session, Integer gameID) {
        if (connections.containsKey(gameID)) {
            var game = connections.get(gameID);
            game.remove(session);
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException {
        String message = new Gson().toJson(notification);
        for (List<Session> lst : connections.values()) {
            if (connections.get(gameID).equals(lst)) {
                broadcastPartTwo(lst, excludeSession, message);
            }
        }
    }

    private void broadcastPartTwo(List<Session> lst, Session excludeSession, String message) throws IOException {
        for (Session c : lst) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(message);
                }
            }
        }
    }
}
