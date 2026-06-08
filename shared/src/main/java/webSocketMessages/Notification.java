package webSocketMessages;

import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {
        JOIN,
        OBSERVE,
        MOVE_MADE,
        LEAVE,
        RESIGN,
        CHECK,
        CHECKMATE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
