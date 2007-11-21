package net.orfjackal.weenyconsole;

import net.orfjackal.weenyconsole.exceptions.InvalidSourceValueException;
import net.orfjackal.weenyconsole.exceptions.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 4.8.2007
 */
public interface ConversionService {

    Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException;
}
