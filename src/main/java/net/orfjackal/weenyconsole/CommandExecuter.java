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

import net.orfjackal.weenyconsole.converters.*;
import net.orfjackal.weenyconsole.exceptions.AmbiguousMethodsException;
import net.orfjackal.weenyconsole.exceptions.CommandExecutionException;
import net.orfjackal.weenyconsole.exceptions.CommandNotFoundException;
import net.orfjackal.weenyconsole.exceptions.CommandTargetException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
public class CommandExecuter {

    private final CommandService target;
    private final ConverterProvider provider = initProvider();

    private static ConverterProvider initProvider() {
        Converter[] converters = new Converter[]{
                // default
                new StringConstructorConverter(),
                // primitive types
                new DelegatingConverter(Boolean.TYPE, Boolean.class),
                new DelegatingConverter(Character.TYPE, Character.class),
                new DelegatingConverter(Byte.TYPE, Byte.class),
                new DelegatingConverter(Short.TYPE, Short.class),
                new DelegatingConverter(Integer.TYPE, Integer.class),
                new DelegatingConverter(Long.TYPE, Long.class),
                new DelegatingConverter(Float.TYPE, Float.class),
                new DelegatingConverter(Double.TYPE, Double.class),
                // special handling for basic types
                new BooleanConverter(),
                new CharacterConverter(),
                new EnumConverter(),
        };
        ConverterProvider provider = new ConverterProvider();
        for (Converter converter : converters) {
            provider.addConverter(converter);
        }
        return provider;
    }

    public CommandExecuter(CommandService target) {
        this.target = target;
    }

    public void addConverter(Converter converter) {
        provider.addConverter(converter);
    }

    /**
     * @throws CommandExecutionException
     */
    public Object execute(String command) {
        try {
            if (command.trim().length() == 0) {
                return null;
            }
            return matchFor(command).invoke(target);

        } catch (CommandExecutionException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw new CommandTargetException(command, e.getTargetException(), e);
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // should never happen - caused by restricted Java VM or a bug
            throw new CommandExecutionException(command, e);
        } catch (RuntimeException e) {
            e.printStackTrace(); // should never happen - caused by a bug in this program
            throw new CommandExecutionException(command, e);
        }
    }

    private Match matchFor(String command) {
        List<Match> matches = allMatchesFor(command);
        if (matches.size() == 1) {
            return matches.get(0);
        }
        if (matches.size() == 0) {
            throw new CommandNotFoundException(command);
        }
        int lengthOfFirst = matches.get(0).method.getName().length();
        int lengthOfSecond = matches.get(1).method.getName().length();
        if (lengthOfFirst > lengthOfSecond) {
            // higher priority for longer names
            return matches.get(0);
        }
        throw new AmbiguousMethodsException(command, methodsFrom(matches));
    }

    private List<Match> allMatchesFor(String command) {
        List<Match> matches = new ArrayList<Match>();
        for (MethodCall methodCall : possibleMethodCalls(command)) {
            matches.addAll(matchesWithPossibleMethods(methodCall));
        }
        return matches;
    }

    private List<MethodCall> possibleMethodCalls(String command) {
        List<MethodCall> results = new ArrayList<MethodCall>();
        String[] words = CommandUtil.wordsFrom(command);
        for (int i = words.length; i > 0; i--) {
            String methodName = CommandUtil.methodNameFrom(words, i);
            if (methodName != null) {
                results.add(new MethodCall(methodName, words, i, words.length - i, provider));
            }
        }
        return results;
    }

    private List<Match> matchesWithPossibleMethods(MethodCall methodCall) {
        List<Match> matches = new ArrayList<Match>();
        for (Method method : possibleMethods()) {
            if (methodCall.matches(method)) {
                matches.add(new Match(methodCall, method));
            }
        }
        return matches;
    }

    private Method[] possibleMethods() {
        List<Method> results = new ArrayList<Method>();
        for (Method method : target.getClass().getMethods()) {
            if (implementsTheMarkerInterface(method)
                    && isPublicInstanceMethod(method)) {
                results.add(method);
            }
        }
        return results.toArray(new Method[results.size()]);
    }

    private static List<Method> methodsFrom(List<Match> matches) {
        List<Method> methods = new ArrayList<Method>();
        for (Match match : matches) {
            methods.add(match.method);
        }
        return methods;
    }

    // TODO: the name "Match" does not describe this class well enough - refactor for better readability 
    private static class Match {

        public final MethodCall methodCall;
        public final Method method;

        public Match(MethodCall methodCall, Method method) {
            this.methodCall = methodCall;
            this.method = method;
        }

        public Object invoke(CommandService target) throws IllegalAccessException, InvocationTargetException {
            return methodCall.invoke(method, target);
        }
    }

    private static boolean implementsTheMarkerInterface(Method method) {
        return CommandService.class.isAssignableFrom(method.getDeclaringClass());
    }

    private static boolean isPublicInstanceMethod(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers());
    }
}
