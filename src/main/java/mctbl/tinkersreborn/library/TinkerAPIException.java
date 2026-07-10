package mctbl.tinkersreborn.library;

public class TinkerAPIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TinkerAPIException() {}

    public TinkerAPIException(String message) {
        super(message);
    }

    public TinkerAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public TinkerAPIException(Throwable cause) {
        super(cause);
    }

    public TinkerAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
