package net.orfjackal.weenyconsole;

import jdave.Specification;
import jdave.Block;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;
import org.jmock.Expectations;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown"})
@RunWith(JDaveRunner.class)
public class ConverterProviderSpec extends Specification<ConverterProvider> {

    public class ProviderWithNoConverters {

        private ConverterProvider provider;

        public ConverterProvider create() {
            provider = new ConverterProvider();
            return provider;
        }

        public void shouldNotProvideAnyConverters() {
            specify(provider.converterFor(Integer.class), should.equal(null));
        }

        public void shouldProvideAConverterAfterItIsFirstAdded() {
            final Converter converter = mock(Converter.class);
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(Integer.class));
                one(converter).setProvider(provider);
            }});
            provider.addConverter(converter);
            specify(provider.converterFor(Integer.class), should.equal(converter));
        }

        public void shouldGiveToTheConverterAccessToTheProvider() {
            final Converter converter = mock(Converter.class);
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(Integer.class));
                one(converter).setProvider(provider);
            }});
            provider.addConverter(converter);
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
            provider.addConverter(integerConverter);
            provider.addConverter(doubleConverter);
            return provider;
        }

        public void shouldProvideAConverterForTheRequestedTargetType() {
            specify(provider.converterFor(Integer.class), should.equal(integerConverter));
            specify(provider.converterFor(Double.class), should.equal(doubleConverter));
        }

        public void afterRemovingAConverterTheProviderShouldNotContainIt() {
            provider.removeConverterFor(Integer.class);
            specify(provider.converterFor(Integer.class), should.equal(null));
        }
    }

    public class ProviderWithManyConvertersInTheSameClassHierarchy {

        private ConverterProvider provider;
        private Converter superConverter;
        private Converter exactConverter;
        private Converter subConverter;

        public ConverterProvider create() {
            provider = new ConverterProvider();
            superConverter = mock(Converter.class, "superConverter");
            exactConverter = mock(Converter.class, "exactConverter");
            subConverter = mock(Converter.class, "subConverter");
            checking(new Expectations() {{
                one(superConverter).supportedTargetType(); will(returnValue(Object.class));
                one(superConverter).setProvider(provider);
                one(exactConverter).supportedTargetType(); will(returnValue(Number.class));
                one(exactConverter).setProvider(provider);
                one(subConverter).supportedTargetType(); will(returnValue(Integer.class));
                one(subConverter).setProvider(provider);
            }});
            provider.addConverter(subConverter);
            provider.addConverter(exactConverter);
            provider.addConverter(superConverter);
            return provider;
        }

        public void shouldFirstlyUseAConverterOfTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        public void shouldSecondlyUseAConverterOfASubClassOfTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        public void shouldThirdlyUseAConverterForASuperClassOfTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(superConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        public void shouldFourthlyFail() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(superConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(TargetTypeNotSupportedException.class));
        }

        public void shouldFailOnFirstStageIfTheSourceValueIsReportedAsInvalid() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new InvalidSourceValueException("1", Number.class)));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(InvalidSourceValueException.class));
        }

        public void shouldFailOnSecondStageIfTheSourceValueIsReportedAsInvalid() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(throwException(new InvalidSourceValueException("1", Number.class)));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(InvalidSourceValueException.class));
        }

        public void shouldFailOnThirdStageIfTheSourceValueIsReportedAsInvalid() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(superConverter).valueOf("1", Number.class); will(throwException(new InvalidSourceValueException("1", Number.class)));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(InvalidSourceValueException.class));
        }

        
    }
}
