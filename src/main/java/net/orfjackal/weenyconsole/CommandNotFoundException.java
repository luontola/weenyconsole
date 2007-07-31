package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(String command) {
        super(command);
    }

    public CommandNotFoundException(String command, Throwable cause) {
        super(command, cause);
    }

    public String getCommand() {
        return getMessage();
    }
}
