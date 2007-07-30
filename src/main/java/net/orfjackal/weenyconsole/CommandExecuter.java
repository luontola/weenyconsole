package net.orfjackal.weenyconsole;

import java.lang.reflect.Constructor;
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
        try {
            Method[] methods = target.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(words[0])) {
                    Class<?>[] types = method.getParameterTypes();
                    Object[] parameters = new Object[words.length - 1];

                    for (int i = 0; i < types.length; i++) {
                        Class<?> type = types[i];
                        String word = words[i + 1];
                        Constructor<?> constructor = type.getConstructor(String.class);
                        parameters[i] = constructor.newInstance(word);
                    }

                    method.invoke(target, parameters);
                    return;
                }
            }
            throw new CommandNotFoundException(command);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
