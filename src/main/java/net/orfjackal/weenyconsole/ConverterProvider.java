package net.orfjackal.weenyconsole;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class ConverterProvider implements Converter {

    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public Converter converterFor(Class<?> targetType) {
        return converters.get(targetType);
    }

    public void add(Converter converter) {
        converters.put(converter.supportedTargetType(), converter);
        converter.setProvider(this);
    }

    @SuppressWarnings({"LoopStatementThatDoesntLoop"})
    public <T> T valueOf(final String sourceValue, final Class<T> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {

        // find a converter for the targetType
        try {
            Converter converter = converterFor(targetType);
            return converter.valueOf(sourceValue, targetType);
        } catch (TargetTypeNotSupportedException e) {
            // FALLTHROUGH
        }

        // find a converter for a subclass of targetType
        for (Class<?> clazz : converters.keySet()) {
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
            return converter.valueOf(sourceValue, targetType);
        }
        throw new TargetTypeNotSupportedException(sourceValue, targetType);
    }

    public Class<?> supportedTargetType() {
        return null;
    }

    public void setProvider(ConverterProvider provider) {
    }
}
