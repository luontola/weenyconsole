package net.orfjackal.weenyconsole;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class ConverterProvider {

    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public Converter findConverterFor(Class<?> targetType) {
        Converter converter = converters.get(targetType);
        while (converter == null && targetType.getSuperclass() != null) {
            targetType = targetType.getSuperclass();
            converter = converters.get(targetType);
        }
        return converter;
    }

    public void add(Converter converter) {
        converters.put(converter.supportedTargetType(), converter);
        converter.setProvider(this);
    }
}
