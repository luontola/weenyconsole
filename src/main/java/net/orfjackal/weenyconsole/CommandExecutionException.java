package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class CommandExecutionException extends RuntimeException {

    private final String command;

    public CommandExecutionException(String command) {
        super(messageFor(command, null));
        this.command = command;
    }

    public CommandExecutionException(String command, Throwable cause) {
        super(messageFor(command, cause), cause);
        this.command = command;
    }

    public CommandExecutionException(String command, String message) {
        super(message);
        this.command = command;
    }

    public CommandExecutionException(String command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    private static String messageFor(String command, Throwable cause) {
        StringBuilder sb = new StringBuilder();
        sb.append("\ncommand failed: ").append(command);
        if (cause != null) {
            sb.append("\n    because of: ").append(cause);
        }
        return sb.toString();
    }

    public String getCommand() {
        return command;
    }
}
