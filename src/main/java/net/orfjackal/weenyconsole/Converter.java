package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public interface Converter {

    Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException;

    Class<?> supportedTargetType();

    void setProvider(ConverterProvider provider);
}
