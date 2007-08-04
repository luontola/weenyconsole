package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.weenyconsole.converters.BooleanConverter;
import net.orfjackal.weenyconsole.converters.DelegatingConverter;
import net.orfjackal.weenyconsole.converters.StringConstructorConverter;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import java.math.BigInteger;

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

        public void shouldNotAllowConvertersWhoseTargetTypeIsNull() {
            final Converter converter = mock(Converter.class);
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(null));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.addConverter(converter);
                }
            }, should.raise(IllegalArgumentException.class));
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
            checking(new Expectations() {{
                one(integerConverter).setProvider(null);
            }});
            provider.removeConverterFor(Integer.class);
            specify(provider.converterFor(Integer.class), should.equal(null));
            specify(provider.converterFor(Double.class), should.equal(doubleConverter));
        }

        public void removingAConverterWhichDoesNotExistShouldExitSilently() {
            provider.removeConverterFor(String.class);
            specify(provider.converterFor(Integer.class), should.equal(integerConverter));
            specify(provider.converterFor(Double.class), should.equal(doubleConverter));
        }

        public void shouldConvertNullToNull() throws ConversionFailedException {
            specify(provider.valueOf(null, Object.class), should.equal(null));
        }

        public void shouldNotConvertNullToAPrimitiveType() {
            provider.addConverter(new DelegatingConverter(int.class, Integer.class));
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf(null, int.class);
                }
            }, should.raise(InvalidSourceValueException.class));
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

        /**
         * A dedicated converter for the target type, if present, is always the best
         * candidate for conversions, so it should have the highest priority. This will
         * also make it possible to override the default conversion for a specific type,
         * if the default {@link StringConstructorConverter} would not produce the
         * desired result (such as in the case of {@link BooleanConverter}).
         */
        public void shouldFirstlyUseAConverterForTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        /**
         * By definition all subclasses of the target type are instances of the target type,
         * so using one of the subclasses' converter should be almost as good as a converter
         * for the exact target type.
         */
        public void shouldSecondlyUseAConverterForASubclassOfTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        /**
         * A generic converter, such as {@link StringConstructorConverter} <em>might</em>
         * be able to handle also the conversions of its subclasses, so it is a good guess
         * to try using the converter of a superclass of the target type.
         */
        public void shouldThirdlyUseAConverterForASuperclassOfTheTargetType() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(subConverter).valueOf("1", Number.class); will(throwException(new TargetTypeNotSupportedException("1", Number.class)));
                one(superConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        /**
         * When no suitable converter could be found, the only option is to report
         * it to the user.
         */
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

        public void shouldSkipFirstStageIfNoConverterWasFound() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).setProvider(null);
                one(subConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            provider.removeConverterFor(Number.class);
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        public void shouldSkipAlsoSecondStageIfNoConverterWasFound() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).setProvider(null);
                one(subConverter).setProvider(null);
                one(superConverter).valueOf("1", Number.class); will(returnValue(1));
            }});
            provider.removeConverterFor(Number.class);
            provider.removeConverterFor(Integer.class);
            specify(provider.valueOf("1", Number.class), should.equal(1));
        }

        public void shouldSkipAlsoThirdStageAndFailIfNoConverterWasFound() {
            checking(new Expectations() {{
                one(exactConverter).setProvider(null);
                one(subConverter).setProvider(null);
                one(superConverter).setProvider(null);
            }});
            provider.removeConverterFor(Number.class);
            provider.removeConverterFor(Integer.class);
            provider.removeConverterFor(Object.class);
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(TargetTypeNotSupportedException.class));
        }

        public void shouldVerifyOnFirstStageThatTheConvertedValueIsAnInstanceOfTheTargetClass() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(returnValue(new Object()));
                one(subConverter).valueOf("1", Number.class); will(returnValue(new BigInteger("1")));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(new BigInteger("1")));
        }

        public void shouldVerifyOnSecondStageThatTheConvertedValueIsAnInstanceOfTheTargetClass() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(returnValue(new Object()));
                one(subConverter).valueOf("1", Number.class); will(returnValue(new Object()));
                one(superConverter).valueOf("1", Number.class); will(returnValue(new BigInteger("1")));
            }});
            specify(provider.valueOf("1", Number.class), should.equal(new BigInteger("1")));
        }

        public void shouldVerifyOnThirdStageThatTheConvertedValueIsAnInstanceOfTheTargetClass() throws ConversionFailedException {
            checking(new Expectations() {{
                one(exactConverter).valueOf("1", Number.class); will(returnValue(new Object()));
                one(subConverter).valueOf("1", Number.class); will(returnValue(new Object()));
                one(superConverter).valueOf("1", Number.class); will(returnValue(new Object()));
            }});
            specify(new Block() {
                public void run() throws Throwable {
                    provider.valueOf("1", Number.class);
                }
            }, should.raise(TargetTypeNotSupportedException.class));
        }
    }
}
