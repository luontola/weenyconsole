package net.orfjackal.weenyconsole;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
public class CommandExecuter {

    private Object target;

    public CommandExecuter(Object target) {
        this.target = target;
    }

    public void execute(String command) throws CommandNotFoundException {
        String[] words = command.split(" ");

        Object[] parameters = new Object[words.length - 1];
        System.arraycopy(words, 1, parameters, 0, parameters.length);

        Class[] parameterTypes = new Class[words.length - 1];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = String.class;
        }
        try {
            String methodName = words[0];
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            method.invoke(target, parameters);

        } catch (NoSuchMethodException e) {
            throw new CommandNotFoundException(command, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
