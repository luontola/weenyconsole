package net.orfjackal.weenyconsole;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;
import org.jmock.Expectations;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class ConverterProviderSpec extends Specification<ConverterProvider> {

    public class ProviderWithNoConverters {

        private ConverterProvider provider;

        public ConverterProvider create() {
            provider = new ConverterProvider();
            return provider;
        }

        public void shouldNotProvideAnyConverters() {
            specify(provider.findConverterFor(Integer.class), should.equal(null));
        }

        public void shouldProvideAConverterAfterItIsFirstAdded() {
            final Converter converter = mock(Converter.class);
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(Integer.class));
                one(converter).setProvider(provider);
            }});
            provider.add(converter);
            specify(provider.findConverterFor(Integer.class), should.equal(converter));
        }
        
        public void shouldGiveToTheConverterAccessToTheProvider() {
            final Converter converter = mock(Converter.class);
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(Integer.class));
                one(converter).setProvider(provider);
            }});
            provider.add(converter);
        }
    }

    public class ProviderWithConverters {

        private ConverterProvider provider;
        private Converter integerConverter;
        private Converter doubleConverter;

        public ConverterProvider create() {
            provider = new ConverterProvider();
            integerConverter = mock(Converter.class, "integerConverter");
            doubleConverter = mock(Converter.class, "doubleConverter");
            checking(new Expectations() {{
                one(integerConverter).supportedTargetType(); will(returnValue(Integer.class));
                one(integerConverter).setProvider(provider);
                one(doubleConverter).supportedTargetType(); will(returnValue(Double.class));
                one(doubleConverter).setProvider(provider);
            }});
            provider.add(integerConverter);
            provider.add(doubleConverter);
            return provider;
        }

        public void shouldProvideAConverterForTheRequestedTargetType() {
            specify(provider.findConverterFor(Integer.class), should.equal(integerConverter));
            specify(provider.findConverterFor(Double.class), should.equal(doubleConverter));
        }
    }

    public class ProviderWithNoExactlyMatchingConverter {

        private ConverterProvider provider;
        private Converter integerConverter;
        private Converter defaultConverter;

        public ConverterProvider create() {
            provider = new ConverterProvider();
            integerConverter = mock(Converter.class, "integerConverter");
            defaultConverter = mock(Converter.class, "defaultConverter");
            checking(new Expectations() {{
                one(integerConverter).supportedTargetType(); will(returnValue(Integer.class));
                one(integerConverter).setProvider(provider);
                one(defaultConverter).supportedTargetType(); will(returnValue(Object.class));
                one(defaultConverter).setProvider(provider);
            }});
            provider.add(integerConverter);
            provider.add(defaultConverter);
            return provider;
        }

        public void shouldTryAConverterForASuperClassOfTheTargetType() {
            specify(provider.findConverterFor(Double.class), should.equal(defaultConverter));
        }
    }
}
