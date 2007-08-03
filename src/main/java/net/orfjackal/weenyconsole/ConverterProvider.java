package net.orfjackal.weenyconsole;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class ConverterProvider {

    private Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();

    public Converter findConverterFor(Class<?> targetType) {
        return converters.get(targetType);
    }

    public void add(Converter converter) {
        converters.put(converter.supportedTargetType(), converter);
        converter.setProvider(this);
    }
}
