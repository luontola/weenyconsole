package net.orfjackal.weenyconsole.exceptions;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
public class CommandNotFoundException extends CommandExecutionException {

    public CommandNotFoundException(String command) {
        super(command, messageFor(command));
    }

    private static String messageFor(String command) {
        return "command not found: " + command;
    }
}
