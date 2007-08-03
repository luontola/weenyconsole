package net.orfjackal.weenyconsole;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
class MethodCall {

    private String methodName;
    private String[] parameters;
    private ConstructorFactory<?> factory;

    public MethodCall(String methodName, String[] srcParameters, int srcPos, int srcLen, ConstructorFactory<?> factory) {
        this.methodName = methodName;
        this.parameters = new String[srcLen];
        System.arraycopy(srcParameters, srcPos, this.parameters, 0, this.parameters.length);
        this.factory = factory;
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
        int count = origSrcValues.length - origDestTypes.length + 1;

        // temporary arrays for varargs, so that we can call convertToType
        String[] srcValues = new String[count];
        Class<?>[] destTypes = new Class<?>[count];
        Object[] destValues = (Object[]) Array.newInstance(destType, count);
        System.arraycopy(origSrcValues, origDestTypes.length - 1, srcValues, 0, srcValues.length);
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
        if (srcValue == null) {
            if (destType.isPrimitive()) {
                throw new ConversionFailedException(srcValue, destType);
            }
            return null;
        }
        if (destType.isPrimitive()) {
            destType = primitiveToWrapperType(destType);
        }
        if (destType.equals(Boolean.class)
                && !srcValue.equals(Boolean.toString(true))
                && !srcValue.equals(Boolean.toString(false))) {
            throw new ConversionFailedException(srcValue, destType);
        }
        if (destType.equals(Character.class) && srcValue.length() == 1) {
            return srcValue.charAt(0);
        }
        if (destType.isEnum()) {
            for (Enum<?> e : (Enum<?>[]) destType.getEnumConstants()) {
                if (e.name().equals(srcValue)) {
                    return e;
                }
            }
        }
        try {
            if (factory != null && destType.equals(factory.typeOfCreatedInstances())) {
                return factory.createNewInstanceFrom(srcValue);
            }
            Constructor<?> constructor = destType.getConstructor(String.class);
            return constructor.newInstance(srcValue);

        } catch (Exception e) {
            throw new ConversionFailedException(srcValue, destType, e);
        }
    }

    private static Map<Class<?>, Class<?>> primitiveWrappers;

    static {
        HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        map.put(Boolean.TYPE, Boolean.class);
        map.put(Character.TYPE, Character.class);
        map.put(Byte.TYPE, Byte.class);
        map.put(Short.TYPE, Short.class);
        map.put(Integer.TYPE, Integer.class);
        map.put(Long.TYPE, Long.class);
        map.put(Float.TYPE, Float.class);
        map.put(Double.TYPE, Double.class);
        primitiveWrappers = Collections.unmodifiableMap(map);
    }

    private static Class<?> primitiveToWrapperType(Class<?> targetType) {
        return primitiveWrappers.get(targetType);
    }
}
