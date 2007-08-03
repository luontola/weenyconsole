package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class ConverterSpec extends Specification<Converter> {

    public class DefaultConversion {

        private DefaultConverter converter;

        public Converter create() {
            converter = new DefaultConverter();
            return converter;
        }

        public void shouldUseTheStringConstructorOfTheClass() throws ConversionFailedException {
            Object o = converter.valueOf("1", Integer.class);
            specify(o, should.equal(1));
        }

        public void shouldThrowAnExceptionIfConversionFails() {
            specify(new Block() {
                public void run() throws Throwable {
                    converter.valueOf("not a number", Integer.class);
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
            return delegator;
        }

//        public void shouldUseAnotherConverterToDoTheConversion() throws ConversionFailedException {
//            checking(new Expectations() {{
//                one(converter).valueOf("foo", Double.class); will(returnValue(1.23));
//            }});
//            delegator.valueOf("foo", Integer.class);
//        }
    }

}
