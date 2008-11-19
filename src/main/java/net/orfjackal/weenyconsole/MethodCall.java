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

import net.orfjackal.weenyconsole.exceptions.ConversionFailedException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
class MethodCall {

    private final String methodName;
    private final String[] parameters;
    private final ConverterProvider provider;

    public MethodCall(String methodName, String[] srcParameters, int srcPos, int srcLen, ConverterProvider provider) {
        this.methodName = methodName;
        this.parameters = new String[srcLen];
        System.arraycopy(srcParameters, srcPos, this.parameters, 0, this.parameters.length);
        this.provider = provider;
    }

    public boolean matches(Method method) {
        return methodHasTheRightName(method)
                && parametersCanBeAssignedTo(method);
    }

    public Object invoke(Method method, Object methodOwner) throws IllegalAccessException, InvocationTargetException {
        return method.invoke(methodOwner, parametersForMethod(method, parameters));
    }

    private boolean methodHasTheRightName(Method method) {
        return method.getName().equals(methodName);
    }

    private boolean parametersCanBeAssignedTo(Method method) {
        return parametersForMethod(method, parameters) != null;
    }

    private Object[] parametersForMethod(Method method, String[] srcValues) {
        // TODO: refactor this method to be cleaner, for example when adding array support
        try {
            Class<?>[] destTypes = method.getParameterTypes();
            Object[] destValues = new Object[destTypes.length];

            int lastIndex = destTypes.length - 1;
            if (srcValues.length < lastIndex) {
                // not enough srcValues for even a varargs method (with zero vararg parameters)
                return null;
            }
            convertToTypes(srcValues, destTypes, destValues, lastIndex);

            if (method.isVarArgs()) {
                // last type is a vararg parameter
                destValues[lastIndex] = convertVarargs(srcValues, destTypes);

            } else if (destTypes.length == srcValues.length) {
                // last type is a normal parameter
                if (lastIndex >= 0) {
                    destValues[lastIndex] = convertToType(srcValues[lastIndex], destTypes[lastIndex]);
                }
            } else {
                // non-vararg method has wrong number of arguments
                return null;
            }
            return destValues;

        } catch (ConversionFailedException e) {
            return null;
        }
    }

    private Object[] convertVarargs(String[] origSrcValues, Class<?>[] origDestTypes) throws ConversionFailedException {
        int varargsIndex = origDestTypes.length - 1;
        int varargsCount = origSrcValues.length - varargsIndex;
        Class<?> destType = origDestTypes[varargsIndex].getComponentType();

        // temporary arrays for varargs, so that we can call convertToType
        String[] srcValues = Arrays.copyOfRange(origSrcValues, varargsIndex, origSrcValues.length);
        Object[] destValues = (Object[]) Array.newInstance(destType, varargsCount);
        Class<?>[] destTypes = new Class<?>[varargsCount];
        Arrays.fill(destTypes, destType);

        convertToTypes(srcValues, destTypes, destValues, destValues.length);
        return destValues;
    }

    private void convertToTypes(String[] srcValues, Class<?>[] destTypes, Object[] destValues, int limit) throws ConversionFailedException {
        for (int i = 0; i < limit; i++) {
            destValues[i] = convertToType(srcValues[i], destTypes[i]);
        }
    }

    private Object convertToType(String srcValue, Class<?> destType) throws ConversionFailedException {
        return provider.valueOf(srcValue, destType);
    }
}
