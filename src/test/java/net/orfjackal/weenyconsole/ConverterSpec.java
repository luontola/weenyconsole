package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.weenyconsole.converters.StringConstructorConverter;
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
}
