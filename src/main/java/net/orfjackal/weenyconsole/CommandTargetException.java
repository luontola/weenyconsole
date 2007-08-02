package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 2.8.2007
 */
public class CommandTargetException extends CommandExecutionException {

    private Throwable targetException;

    public CommandTargetException(String command, Throwable targetException, Throwable cause) {
        super(command, messageFor(targetException), cause);
        this.targetException = targetException;
    }

    private static String messageFor(Throwable target) {
        return "exception was thrown: " + target.getClass().getName() + ": " + target.getMessage();
    }

    public Throwable getTargetException() {
        return targetException;
    }
}
