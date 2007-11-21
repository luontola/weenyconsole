package net.orfjackal.weenyconsole.exceptions;

/**
 * @author Esko Luontola
 * @since 1.8.2007
 */
public class ConversionFailedException extends Exception {

    public ConversionFailedException(String sourceValue, Class<?> targetType) {
        super(messageFor(sourceValue, targetType));
    }

    public ConversionFailedException(String sourceValue, Class<?> targetType, Throwable cause) {
        super(messageFor(sourceValue, targetType), cause);
    }

    private static String messageFor(String sourceValue, Class<?> targetType) {
        return "Can not convert " + sourceValue + " to " + targetType;
    }
}
