package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    public ResponseException(String message) {
        super(message);
    }
}
