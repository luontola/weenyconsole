package net.orfjackal.weenyconsole.exceptions;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class MalformedCommandException extends CommandExecutionException {

    public MalformedCommandException(String command, String reason, int errorPos) {
        super(command, messageFor(command, reason, errorPos));
    }

    private static String messageFor(String command, String reason, int errorPos) {
        StringBuilder message = new StringBuilder((command.length() + reason.length()) * 3);
        message.append(reason).append(": ").append(command).append('\n');
        int padding = (reason.length() + 2 + errorPos);
        for (int i = 0; i < padding; i++) {
            message.append(' ');
        }
        message.append('^');
        return message.toString();
    }
}
