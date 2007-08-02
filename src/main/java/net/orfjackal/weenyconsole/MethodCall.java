package net.orfjackal.weenyconsole;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
class MethodCall {

    private String methodName;
    private String[] parameters;
    private ConstructorFactory factory;

    public MethodCall(String methodName, String[] srcParameters, int srcPos, int srcLen, ConstructorFactory factory) {
        this.methodName = methodName;
        this.parameters = new String[srcLen];
        System.arraycopy(srcParameters, srcPos, this.parameters, 0, this.parameters.length);
        this.factory = factory;
    }

    public boolean matches(Method method) {
        return methodHasTheRightName(method)
                && parametersCanBeAssignedTo(method);
    }

    public void invoke(Method method, Object methodOwner) throws IllegalAccessException, InvocationTargetException {
        method.invoke(methodOwner, parametersForMethod(method, parameters));
    }

    private boolean methodHasTheRightName(Method method) {
        return method.getName().equals(methodName);
    }

    private boolean parametersCanBeAssignedTo(Method method) {
        return parametersForMethod(method, parameters) != null;
    }

    private Object[] parametersForMethod(Method method, String[] words) {
        try {


            Class<?>[] types = method.getParameterTypes();
            System.out.println("words = " + Arrays.toString(words));
            System.out.println("types = " + Arrays.toString(types));
            if (words.length < (types.length - 1)) {
                return null;
            }

            Object[] parameters = new Object[types.length];
            for (int i = 0; i < (types.length - 1); i++) { // surely non-vararg parameters
                parameters[i] = convertToType(words[i], types[i]);
            }


            if (method.isVarArgs()) {
                Class<?> varargArrayType = types[types.length - 1];
                Class<?> varargType = varargArrayType.getComponentType();
                List<Object> varargParams = new ArrayList<Object>();
                for (int i = (types.length - 1); i < words.length; i++) { // vararg parameters
                    varargParams.add(convertToType(words[i], varargType));
                }
                parameters[types.length - 1] = varargParams.toArray((Object[]) Array.newInstance(varargType, 0));

            } else if (types.length == words.length) {
                int i = types.length - 1;
                if (i >= 0) {
                    parameters[i] = convertToType(words[i], types[i]);
                }
            } else {
                return null;
            }

            System.out.println("parameters = " + Arrays.toString(parameters));

            return parameters;

        } catch (ConversionFailedException e) {
            return null;
        }
    }

    private Object convertToType(String sourceValue, Class<?> targetType) throws ConversionFailedException {
        if (sourceValue == null) {
            if (targetType.isPrimitive()) {
                throw new ConversionFailedException(sourceValue, targetType);
            }
            return null;
        }
        if (targetType.isPrimitive()) {
            targetType = primitiveToWrapperType(targetType);
        }
        if (targetType.equals(Boolean.class)
                && !sourceValue.equals(Boolean.toString(true))
                && !sourceValue.equals(Boolean.toString(false))) {
            throw new ConversionFailedException(sourceValue, targetType);
        }
        if (targetType.equals(Character.class) && sourceValue.length() == 1) {
            return sourceValue.charAt(0);
        }
        if (targetType.isEnum()) {
            for (Enum<?> e : (Enum<?>[]) targetType.getEnumConstants()) {
                if (e.name().equals(sourceValue)) {
                    return e;
                }
            }
        }
        try {
            if (factory != null && factory.canCreateInstancesOf(targetType)) {
                return factory.createNewInstanceFrom(sourceValue);
            }
            Constructor<?> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(sourceValue);

        } catch (Exception e) {
            throw new ConversionFailedException(sourceValue, targetType, e);
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
