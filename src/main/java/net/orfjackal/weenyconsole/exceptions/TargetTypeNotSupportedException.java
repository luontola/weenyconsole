package net.orfjackal.weenyconsole.exceptions;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class TargetTypeNotSupportedException extends ConversionFailedException {

    public TargetTypeNotSupportedException(String sourceValue, Class<?> targetType) {
        super(sourceValue, targetType);
    }

    public TargetTypeNotSupportedException(String sourceValue, Class<?> targetType, Throwable cause) {
        super(sourceValue, targetType, cause);
    }
}
