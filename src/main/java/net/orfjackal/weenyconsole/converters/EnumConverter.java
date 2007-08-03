package net.orfjackal.weenyconsole.converters;

import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.ConverterProvider;
import net.orfjackal.weenyconsole.InvalidSourceValueException;
import net.orfjackal.weenyconsole.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 4.8.2007
 */
public class EnumConverter implements Converter {

    public Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
        if (!targetType.isEnum()) {
            throw new TargetTypeNotSupportedException(sourceValue, targetType);
        }
        for (Enum<?> e : (Enum<?>[]) targetType.getEnumConstants()) {
            if (e.name().equals(sourceValue)) {
                return e;
            }
        }
        throw new InvalidSourceValueException(sourceValue, targetType);
    }

    public Class<?> supportedTargetType() {
        return Enum.class;
    }

    public void setProvider(ConverterProvider provider) {
    }
}
