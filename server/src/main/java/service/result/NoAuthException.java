package service.result;

public class NoAuthException extends RuntimeException {
    public NoAuthException(String message) {
        super(message);
    }
}
