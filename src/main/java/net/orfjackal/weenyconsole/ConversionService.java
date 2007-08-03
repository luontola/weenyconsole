package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 4.8.2007
 */
public interface ConversionService {

    Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException;
}
