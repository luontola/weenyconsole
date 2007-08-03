package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public class DelegatingConverter implements Converter {

    private Class<Integer> delegateFrom;
    private Class<Double> delegateTo;
    private ConverterProvider provider;

    public DelegatingConverter(Class<Integer> delegateFrom, Class<Double> delegateTo) {
        this.delegateFrom = delegateFrom;
        this.delegateTo = delegateTo;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T valueOf(String sourceValue, Class<T> targetType) throws TargetTypeNotSupportedException, InvalidSourceValueException {
        Converter realConverter = provider.converterFor(delegateTo);
        if (realConverter == null) {
            throw new TargetTypeNotSupportedException(sourceValue, targetType);
        }
        return (T) realConverter.valueOf(sourceValue, delegateTo);
    }

    public Class<?> supportedTargetType() {
        return delegateFrom;
    }

    public void setProvider(ConverterProvider provider) {
        this.provider = provider;
    }
}
