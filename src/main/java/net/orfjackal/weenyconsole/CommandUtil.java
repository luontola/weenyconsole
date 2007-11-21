package net.orfjackal.weenyconsole;

import net.orfjackal.weenyconsole.exceptions.MalformedCommandException;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esko Luontola
 * @since 22.11.2007
 */
public class CommandUtil {

    private CommandUtil() {
    }

    public static String[] wordsFrom(String command) {
        List<String> finishedWords = new ArrayList<String>();
        String word = "";
        boolean escaped = false;
        boolean insideQuotes = false;
        for (int currentPos = 0; currentPos < command.length(); currentPos++) {
            char c = command.charAt(currentPos);
            if (escaped) {
                word = unescape(c, word, finishedWords, command, currentPos);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (Character.isWhitespace(c) && !insideQuotes) {
                if (word.length() > 0) {
                    finishedWords.add(word);
                }
                word = "";
            } else {
                word = word + c;
            }
        }
        if (insideQuotes) {
            throw new MalformedCommandException(command, "double quote expected", command.length());
        }
        if (escaped) {
            throw new MalformedCommandException(command, "escape sequence expected", command.length());
        }
        if (word.length() > 0) {
            finishedWords.add(word);
        }
        assert finishedWords.size() > 0;
        return finishedWords.toArray(new String[finishedWords.size()]);
    }

    public static String methodNameFrom(String[] words, int wordsFromStart) {
        String methodName = "";
        for (int i = 0; i < wordsFromStart; i++) {
            String word = words[i];
            if (word == null) {
                return null;
            }
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

    private static String unescape(char escaped, String currentWord, List<String> finishedWords,
                                   String command, int currentPos) {
        Character unescaped;
        if (escaped == ' ') {
            unescaped = ' ';
        } else if (escaped == '\\') {
            unescaped = '\\';
        } else if (escaped == '"') {
            unescaped = '"';
        } else if (escaped == 'n') {
            unescaped = '\n';
        } else if (escaped == 't') {
            unescaped = '\t';
        } else if (escaped == '0') {
            unescaped = null;
        } else {
            throw new MalformedCommandException(command, "escape sequence expected", currentPos);
        }
        if (unescaped != null) {
            currentWord = currentWord + unescaped;
        } else if (currentWord.length() == 0) {
            finishedWords.add(null);
        } else {
            throw new MalformedCommandException(command, "null not allowed here", currentPos);
        }
        return currentWord;
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
}
