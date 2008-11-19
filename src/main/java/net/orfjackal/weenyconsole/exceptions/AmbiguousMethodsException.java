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

package net.orfjackal.weenyconsole.exceptions;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
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
        Collections.sort(methods, new MethodComparator());
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

    private static class MethodComparator implements Comparator<Method> {

        public int compare(Method m1, Method m2) {
            int cmp = m1.getName().compareTo(m2.getName());
            if (cmp != 0) {
                return cmp;
            }
            Class<?>[] types1 = m1.getParameterTypes();
            Class<?>[] types2 = m2.getParameterTypes();
            for (int i = 0; i < types1.length && i < types2.length; i++) {
                cmp = types1[i].getName().compareTo(types2[i].getName());
                if (cmp != 0) {
                    return cmp;
                }
            }
            return types1.length - types2.length; // shortest first
        }
    }
}
