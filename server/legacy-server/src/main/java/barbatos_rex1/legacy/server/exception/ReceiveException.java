package barbatos_rex1.legacy.server.exception;

public class ReceiveException extends ProtocolException {
    public ReceiveException() {
    }

    public ReceiveException(String message) {
        super(message);
    }

    public ReceiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiveException(Throwable cause) {
        super(cause);
    }

    protected ReceiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
