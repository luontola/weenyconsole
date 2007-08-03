package net.orfjackal.weenyconsole;

import java.lang.reflect.Constructor;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class StringConstructorConverter implements Converter {

    public <T> T valueOf(String sourceValue, Class<T> targetType) throws ConversionFailedException {
        try {
            Constructor<T> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(sourceValue);
        } catch (Exception e) {
            throw new ConversionFailedException(sourceValue, targetType, e);
        }
    }

    public Class<?> supportedTargetType() {
        return Object.class;
    }

    public void setProvider(ConverterProvider provider) {
    }
}
