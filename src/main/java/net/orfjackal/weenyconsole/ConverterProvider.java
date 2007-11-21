package net.orfjackal.weenyconsole;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class ConverterProvider implements ConversionService {

    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public Converter converterFor(Class<?> targetType) {
        return converters.get(targetType);
    }

    public void addConverter(Converter converter) {
        Class<?> targetType = converter.supportedTargetType();
        if (targetType == null) {
            throw new IllegalArgumentException("supportedTargetType() returned null: " + converter);
        }
        converters.put(targetType, converter);
        converter.setProvider(this);
    }

    public void removeConverterFor(Class<?> targetType) {
        Converter converter = converters.remove(targetType);
        if (converter != null) {
            converter.setProvider(null);
        }
    }

    private Set<Class<?>> supportedTargetTypes() {
        return Collections.unmodifiableSet(converters.keySet());
    }

    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        if (convertsToNull(targetType, sourceValue)) {
            return null;
        }
        try {
            return convertUsing(converterFor(targetType), sourceValue, targetType);
        } catch (TargetTypeNotSupportedException e) {
            // FALLTHROUGH
        }
        try {
            return convertUsingConverterForSubclassOf(targetType, sourceValue);
        } catch (TargetTypeNotSupportedException e) {
            // FALLTHROUGH
        }
        return convertUsingConverterForSuperclassOf(targetType, sourceValue);
    }

    private static boolean convertsToNull(Class<?> targetType, String sourceValue)
            throws InvalidSourceValueException {
        if (sourceValue == null) {
            if (!targetType.isPrimitive()) {
                return true;
            }
            throw new InvalidSourceValueException(sourceValue, targetType);
        }
        return false;
    }

    private Object convertUsingConverterForSubclassOf(Class<?> targetType, String sourceValue) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        for (Class<?> clazz : supportedTargetTypes()) {
            try {
                if (targetType.isAssignableFrom(clazz) && !targetType.equals(clazz)) {
                    return convertUsing(converterFor(clazz), sourceValue, targetType);
                }
            } catch (TargetTypeNotSupportedException e) {
                // FALLTHROUGH
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private Object convertUsingConverterForSuperclassOf(Class<?> targetType, String sourceValue) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        for (Class<?> clazz = targetType.getSuperclass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                return convertUsing(converterFor(clazz), sourceValue, targetType);
            } catch (TargetTypeNotSupportedException e) {
                // FALLTHROUGH
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private static Object convertUsing(Converter converter, String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        if (converter != null) {
            Object o = converter.valueOf(sourceValue, targetType);
            if (targetType.isAssignableFrom(o.getClass()) || canBeUnboxed(o.getClass(), targetType)) {
                return o;
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private static final Map<Class<?>, Class<?>> wrapperTypes;

    static {
        Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        map.put(Boolean.TYPE, Boolean.class);
        map.put(Character.TYPE, Character.class);
        map.put(Byte.TYPE, Byte.class);
        map.put(Short.TYPE, Short.class);
        map.put(Integer.TYPE, Integer.class);
        map.put(Long.TYPE, Long.class);
        map.put(Float.TYPE, Float.class);
        map.put(Double.TYPE, Double.class);
        wrapperTypes = Collections.unmodifiableMap(map);
    }

    private static boolean canBeUnboxed(Class<?> fromWrapperType, Class<?> toPrimitiveType) {
        Class<?> wrapperType = wrapperTypes.get(toPrimitiveType);
        return (wrapperType != null && wrapperType.equals(fromWrapperType));
    }
}
