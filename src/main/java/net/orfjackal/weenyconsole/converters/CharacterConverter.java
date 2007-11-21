package net.orfjackal.weenyconsole.converters;

import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.exceptions.InvalidSourceValueException;
import net.orfjackal.weenyconsole.exceptions.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 4.8.2007
 */
public class CharacterConverter implements Converter {

    public Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        if (sourceValue.length() == 1) {
            return sourceValue.charAt(0);
        }
        throw new InvalidSourceValueException(sourceValue, targetType);
    }

    public Class<?> supportedTargetType() {
        return Character.class;
    }

    public void setProvider(ConversionService provider) {
    }
}
