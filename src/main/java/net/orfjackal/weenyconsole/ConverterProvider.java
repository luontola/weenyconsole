package net.orfjackal.weenyconsole;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class ConverterProvider implements Converter {

    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public Converter converterFor(Class<?> targetType) {
        return converters.get(targetType);
    }

    public void addConverter(Converter converter) {
        converters.put(converter.supportedTargetType(), converter);
        converter.setProvider(this);
    }

    public void removeConverterFor(Class<?> targetType) {
        Converter converter = converters.remove(targetType);
        if (converter != null) {
            converter.setProvider(null);
        }
    }

    @SuppressWarnings({"LoopStatementThatDoesntLoop"})
    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {

        // find a converter for the targetType
        try {
            Converter converter = converterFor(targetType);
            if (converter != null) {
                return converter.valueOf(sourceValue, targetType);
            }
        } catch (TargetTypeNotSupportedException e) {
            // FALLTHROUGH
        }

        // find a converter for a subclass of targetType
        for (Class<?> clazz : supportedTargetTypes()) {
            if (targetType.isAssignableFrom(clazz) && !targetType.equals(clazz)) {
                try {
                    Converter converter = converterFor(clazz);
                    return converter.valueOf(sourceValue, targetType);
                } catch (TargetTypeNotSupportedException e) {
                    // FALLTHROUGH
                }
            }
        }

        // find a converter for a superclass of targetType
        for (Class<?> clazz = targetType.getSuperclass(); clazz != null; clazz = clazz.getSuperclass()) {
            Converter converter = converterFor(clazz);
            if (converter != null) {
                return converter.valueOf(sourceValue, targetType);
            }
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    private Set<Class<?>> supportedTargetTypes() {
        return Collections.unmodifiableSet(converters.keySet());
    }

    public Class<?> supportedTargetType() {
        throw new UnsupportedOperationException();
    }

    public void setProvider(ConverterProvider provider) {
        throw new UnsupportedOperationException();
    }
}
