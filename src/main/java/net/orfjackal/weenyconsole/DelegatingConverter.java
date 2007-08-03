package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class DelegatingConverter implements Converter{
    
    public DelegatingConverter(Class<Integer> sourceType, Class<Double> targetType) {

    }

    public <T> T valueOf(String sourceValue, Class<T> targetType) throws ConversionFailedException {
        return null;
    }

    public Class<?> supportedTargetType() {
        return null;
    }
}
