package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class CommandExecutionException extends RuntimeException {

    private String command;

    public CommandExecutionException(String command) {
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

    public CommandExecutionException(String command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
