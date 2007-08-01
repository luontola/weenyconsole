package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class MalformedCommandException extends CommandExecutionException {

    public MalformedCommandException(String command, String reason, int errorPos) {
        super(command, messageFor(command, reason, errorPos));
    }

    private static String messageFor(String command, String reason, int errorPos) {
        return null;
    }
}
