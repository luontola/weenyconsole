package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
public interface Converter extends ConversionService {

    Class<?> supportedTargetType();

    void setProvider(ConverterProvider provider);
}
