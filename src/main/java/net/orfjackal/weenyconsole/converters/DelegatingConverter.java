package net.orfjackal.weenyconsole.converters;

import net.orfjackal.weenyconsole.Converter;
import net.orfjackal.weenyconsole.ConverterProvider;
import net.orfjackal.weenyconsole.InvalidSourceValueException;
import net.orfjackal.weenyconsole.TargetTypeNotSupportedException;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class DelegatingConverter implements Converter {

    private Class<?> delegateFrom;
    private Class<?> delegateTo;
    private ConverterProvider provider;

    public DelegatingConverter(Class<?> delegateFrom, Class<?> delegateTo) {
        if (delegateFrom.isAssignableFrom(delegateTo)) {
            throw new IllegalArgumentException(delegateTo.getName() + " is a subclass of " + delegateFrom.getName());
        }
        if (delegateTo.isAssignableFrom(delegateFrom)) {
            throw new IllegalArgumentException(delegateFrom.getName() + " is a subclass of " + delegateTo.getName());
        }
        this.delegateFrom = delegateFrom;
        this.delegateTo = delegateTo;
    }

    @SuppressWarnings({"unchecked"})
    public Object valueOf(String sourceValue, Class<?> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        return provider.valueOf(sourceValue, delegateTo);
    }

    public Class<?> supportedTargetType() {
        return delegateFrom;
    }

    public void setProvider(ConverterProvider provider) {
        this.provider = provider;
    }
}
