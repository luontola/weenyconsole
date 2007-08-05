package net.orfjackal.weenyconsole.converters;

import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.InvalidSourceValueException;
import net.orfjackal.weenyconsole.TargetTypeNotSupportedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class StringConstructorConverter implements Converter {

    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        try {
            Constructor<?> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(sourceValue);
        } catch (IllegalAccessException e) {
            throw new TargetTypeNotSupportedException(sourceValue, targetType, e);
        } catch (NoSuchMethodException e) {
            throw new TargetTypeNotSupportedException(sourceValue, targetType, e);
        } catch (InstantiationException e) {
            throw new TargetTypeNotSupportedException(sourceValue, targetType, e);
        } catch (InvocationTargetException e) {
            throw new InvalidSourceValueException(sourceValue, targetType, e);
        }
    }

    public Class<?> supportedTargetType() {
        return Object.class;
    }

    public void setProvider(ConversionService provider) {
    }
}
