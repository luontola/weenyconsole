package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public interface Converter {

    <T> T valueOf(String sourceValue, Class<T> targetType) throws ConversionFailedException;

    Class<?> supportedTargetType();

    void setProvider(ConverterProvider provider);
}
