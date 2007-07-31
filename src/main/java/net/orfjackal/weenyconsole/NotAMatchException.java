package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class NotAMatchException extends Throwable {

    public NotAMatchException() {
    }

    public NotAMatchException(String message) {
        super(message);
    }

    public NotAMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAMatchException(Throwable cause) {
        super(cause);
    }
}
