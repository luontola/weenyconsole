/*
 * This file is part of WeenyConsole <http://www.orfjackal.net/>
 *
 * Copyright (c) 2007-2008, Esko Luontola. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
