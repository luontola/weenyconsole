package net.orfjackal.weenyconsole;

import javax.lang.model.SourceVersion;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        try {
            String[] words = separateWords(command);
            List<Possibility> possibilities = possibleMethodCalls(words);
            Method[] methods = target.getClass().getMethods();

            for (Possibility possible : possibilities) {
                for (Method method : methods) {
                    if (methodHasTheRightName(method, possible.methodName)) {
                        Object[] parameters = parametersForMethod(method, possible.parameters);
                        if (parameters != null) {
//                            System.out.println("method = " + method);
//                            System.out.println("parameters = " + Arrays.asList(parameters));
                            method.invoke(target, parameters);
                            return;
                        }
                    }
                }
            }
            throw new CommandNotFoundException(command);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Possibility> possibleMethodCalls(String[] words) {
        List<Possibility> possibilities = new ArrayList<Possibility>();
        for (int wordsFromStart = words.length; wordsFromStart > 0; wordsFromStart--) {
            String name1 = combineToMethodName(words, wordsFromStart);
            if (name1 != null) {
                possibilities.add(new Possibility(name1, words, wordsFromStart));
            }
        }
        return possibilities;
    }

    private static Object[] parametersForMethod(Method method, String[] words) {
        try {
            Class<?>[] types = method.getParameterTypes();
            Object[] parameters = new Object[words.length];
            for (int i = 0; i < types.length; i++) {
                parameters[i] = convertToType(words[i], types[i]);
            }
            return parameters;

        } catch (NotAMatchException e) {
//            e.printStackTrace();
            return null;
        }
    }

    private static boolean methodHasTheRightName(Method method, String name) {
        return method.getName().equals(name);
    }

    private static String combineToMethodName(String[] words, int wordsFromStart) {
        String methodName = "";
        for (int i = 0; i < wordsFromStart; i++) {
            String word = words[i];
            if (i > 0) {
                word = capitalize(word);
            }
            methodName += word;
        }
        if (isJavaMethodIdentifier(methodName)) {
            return methodName;
        }
        return null;
    }

    /**
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.8">JLS §3.8</a>
     */
    private static boolean isJavaMethodIdentifier(String s) {
        return SourceVersion.isIdentifier(s) && !SourceVersion.isKeyword(s);
    }

    private static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static String[] separateWords(String command) {
        List<String> words = new ArrayList<String>();
        String word = "";
        boolean escaped = false;
        boolean insideQuotes = false;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (escaped) {
                if (c == 'n') {
                    c = '\n';
                } else if (c == 't') {
                    c = '\t';
                }
                word += c;
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (Character.isWhitespace(c) && !insideQuotes) {
                if (word.length() > 0) {
                    words.add(word);
                }
                word = "";
            } else {
                word += c;
            }
        }
        if (insideQuotes) {
            throw new IllegalArgumentException("Missing a double quote: " + command);
        }
        if (escaped) {
            throw new IllegalArgumentException("Incomplete use of the \\ character: " + command);
        }
        if (word.length() > 0) {
            words.add(word);
        }
        return words.toArray(new String[words.size()]);
    }

    private static Object convertToType(String sourceValue, Class<?> targetType) throws NotAMatchException {
        if (targetType.isPrimitive()) {
            targetType = primitiveToWrapperType(targetType.getName());
        }
        if (targetType.equals(Boolean.class)
                && !sourceValue.equals(Boolean.toString(true))
                && !sourceValue.equals(Boolean.toString(false))) {
            throw new NotAMatchException("Can not convert " + sourceValue + " to " + targetType);
        }
        try {
            Constructor<?> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(sourceValue);
        } catch (Exception e) {
            throw new NotAMatchException("Can not convert " + sourceValue + " to " + targetType, e);
        }
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

    private static class Possibility {
        public String methodName;
        public String[] parameters;

        public Possibility(String methodName, String[] words, int wordsFromStart) {
            String[] parameters = new String[words.length - wordsFromStart];
            System.arraycopy(words, wordsFromStart, parameters, 0, parameters.length);
            this.methodName = methodName;
            this.parameters = parameters;
        }
    }
}
