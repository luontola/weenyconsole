package net.orfjackal.weenyconsole.converters;

import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.InvalidSourceValueException;
import net.orfjackal.weenyconsole.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 4.8.2007
 */
public class BooleanConverter implements Converter {

    public Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        if (sourceValue.equals(Boolean.toString(true))) {
            return Boolean.TRUE;
        } else if (sourceValue.equals(Boolean.toString(false))) {
            return Boolean.FALSE;
        }
        throw new InvalidSourceValueException(sourceValue, targetType);
    }

    public Class<?> supportedTargetType() {
        return Boolean.class;
    }

    public void setProvider(ConversionService provider) {
    }
}
