package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

import java.awt.*;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class ConverterSpec extends Specification<Converter> {

    public class DefaultConversion {

        private StringConstructorConverter converter;

        public Converter create() {
            converter = new StringConstructorConverter();
            return converter;
        }

        public void shouldUseTheStringConstructorOfTheClass() throws ConversionFailedException {
            specify(converter.valueOf("1", Integer.class), should.equal(1));
        }

        public void shouldFailIfTheValueCanNotBeConverted() {
            specify(new Block() {
                public void run() throws Throwable {
                    converter.valueOf("not a number", Integer.class);
                }
            }, should.raise(ConversionFailedException.class));
        }

        public void shouldFailIfTheTargetClassHasNoStringConstructor() {
            specify(new Block() {
                public void run() throws Throwable {
                    converter.valueOf("1,2", Point.class);
                }
            }, should.raise(ConversionFailedException.class));
        }
    }

    public class DelegatedConversion {

        private DelegatingConverter delegator;
        private Converter converter;

        public Converter create() {
            delegator = new DelegatingConverter(Integer.class, Double.class);
            converter = mock(Converter.class);
            final ConverterProvider provider = new ConverterProvider();
            checking(new Expectations() {{
                one(converter).supportedTargetType(); will(returnValue(Double.class));
                one(converter).setProvider(provider);
            }});
            provider.add(delegator);
            provider.add(converter);
            return delegator;
        }

        public void shouldUseAnotherConverterToDoTheConversion() throws ConversionFailedException {
            checking(new Expectations() {{
                one(converter).valueOf("foo", Double.class); will(returnValue(1.23));
            }});
            delegator.valueOf("foo", Integer.class);
        }
    }

}
