package net.orfjackal.weenyconsole;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String[] words = separateWords(command);
        try {
            Method[] methods = target.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(words[0])) {
                    Class<?>[] types = method.getParameterTypes();
                    Object[] parameters = new Object[words.length - 1];

                    for (int i = 0; i < types.length; i++) {
                        parameters[i] = convertToType(words[i + 1], types[i]);
                    }
                    System.out.println("method = " + method);
                    System.out.println("parameters = " + Arrays.asList(parameters));
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

    private static String[] separateWords(String command) {
        List<String> words = new ArrayList<String>();
        String word = "";
        boolean insideQuotes = false;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (Character.isWhitespace(c) && !insideQuotes) {
                if (word.length() > 0) {
                    words.add(word);
                }
                word = "";
            } else {
                word = word + c;
            }
        }
        if (word.length() > 0) {
            words.add(word);
        }
        return words.toArray(new String[words.size()]);
    }

    private static Object convertToType(String sourceValue, Class<?> targetType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (targetType.isPrimitive()) {
            targetType = primitiveToWrapperType(targetType.getName());
        }
        Constructor<?> constructor = targetType.getConstructor(String.class);
        return constructor.newInstance(sourceValue);
    }

    private static Class<?> primitiveToWrapperType(String name) {
        Class<?> wrapper;
        if (name.equals("boolean")) {
            wrapper = Boolean.class;
        } else if (name.equals("byte")) {
            wrapper = Byte.class;
        } else if (name.equals("char")) {
            wrapper = Character.class;
        } else if (name.equals("short")) {
            wrapper = Short.class;
        } else if (name.equals("int")) {
            wrapper = Integer.class;
        } else if (name.equals("long")) {
            wrapper = Long.class;
        } else if (name.equals("float")) {
            wrapper = Float.class;
        } else if (name.equals("double")) {
            wrapper = Double.class;
        } else {
            throw new IllegalArgumentException(name);
        }
        return wrapper;
    }
}
