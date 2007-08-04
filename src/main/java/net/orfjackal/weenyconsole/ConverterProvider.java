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

    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        if (sourceValue == null) {
            if (targetType.isPrimitive()) {
                throw new InvalidSourceValueException(sourceValue, targetType);
            }
            return null;
        }

        // use the converter of targetType
        try {
            return convertUsing(converterFor(targetType), sourceValue, targetType);
        } catch (TargetTypeNotSupportedException e) {
            // FALLTHROUGH
        }

        // use the converter of a subclass of targetType
        for (Class<?> clazz : supportedTargetTypes()) {
            if (targetType.isAssignableFrom(clazz) && !targetType.equals(clazz)) {
                try {
                    return convertUsing(converterFor(clazz), sourceValue, targetType);
                } catch (TargetTypeNotSupportedException e) {
                    // FALLTHROUGH
                }
            }
        }

        // use the converter of a superclass of targetType
        for (Class<?> clazz = targetType.getSuperclass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                return convertUsing(converterFor(clazz), sourceValue, targetType);
            } catch (TargetTypeNotSupportedException e) {
                // FALLTHROUGH
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private Object convertUsing(Converter converter, String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        if (converter != null) {
            Object o = converter.valueOf(sourceValue, targetType);
            if (targetType.isAssignableFrom(o.getClass()) || canBeUnboxed(targetType, o.getClass())) {
                return o;
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private boolean canBeUnboxed(Class<?> targetType, Class<?> sourceType) {
        try {
            if (targetType.isPrimitive() && !sourceType.isPrimitive()) {
                Class<?> primitiveType = (Class<?>) sourceType.getField("TYPE").get(null);
                return primitiveType.equals(targetType);
            }
        } catch (RuntimeException e) {
            // FALLTHROUGH
        } catch (NoSuchFieldException e) {
            // FALLTHROUGH
        } catch (IllegalAccessException e) {
            // FALLTHROUGH
        }
        return false;
    }

    private Set<Class<?>> supportedTargetTypes() {
        return Collections.unmodifiableSet(converters.keySet());
    }
}
