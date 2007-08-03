package net.orfjackal.weenyconsole;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class AmbiguousMethodsException extends CommandExecutionException {

    public AmbiguousMethodsException(String command, List<Method> methods) {
        super(command, messageFor(command, methods));
    }

    private static String messageFor(String command, List<Method> methods) {
        StringBuilder sb = new StringBuilder();
        sb.append("   command failed: ").append(command);
        sb.append("\nambiguous methods: ").append(toString(methods.get(0)));
        for (int i = 1; i < methods.size(); i++) {
            sb.append("\n                   ").append(toString(methods.get(i)));
        }
        return sb.toString();
    }

    /**
     * Simplified from {@link java.lang.reflect.Method#toString()}
     */
    private static String toString(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName()).append("(");
        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i].getName());
            if (i < (params.length - 1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
