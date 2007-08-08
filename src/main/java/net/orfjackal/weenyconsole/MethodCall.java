package net.orfjackal.weenyconsole;

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
        Class<?> destType = origDestTypes[origDestTypes.length - 1].getComponentType();
        int varargsLength = origSrcValues.length - origDestTypes.length + 1;

        // temporary arrays for varargs, so that we can call convertToType
        String[] srcValues = Arrays.copyOfRange(origSrcValues, origDestTypes.length - 1, origSrcValues.length);
        Object[] destValues = (Object[]) Array.newInstance(destType, varargsLength);
        Class<?>[] destTypes = new Class<?>[varargsLength];
        Arrays.fill(destTypes, destType);

        convertToTypes(srcValues, destTypes, destValues, destTypes.length);
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
