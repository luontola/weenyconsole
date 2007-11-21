package net.orfjackal.weenyconsole.converters;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import net.orfjackal.weenyconsole.ConversionService;
import net.orfjackal.weenyconsole.exceptions.InvalidSourceValueException;
import net.orfjackal.weenyconsole.exceptions.TargetTypeNotSupportedException;
import org.jmock.Expectations;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 3.8.2007
 */
@RunWith(JDaveRunner.class)
public class DelegatingConverterSpec extends Specification<DelegatingConverter> {

    public class ADelegatingConverter {

        private ConversionService provider;
        private DelegatingConverter delegator;

        public DelegatingConverter create() {
            provider = mock(ConversionService.class, "provider");
            delegator = new DelegatingConverter(int.class, Integer.class);
            delegator.setProvider(provider);
            return null;
        }

        public void shouldDelegateConversionsOfOneTypeToTheConverterOfAnotherType() throws TargetTypeNotSupportedException, InvalidSourceValueException {
            checking(new Expectations(){{
                one(provider).valueOf("1", Integer.class); will(returnValue(1));
            }});
            specify(delegator.supportedTargetType(), should.equal(int.class));
            specify(delegator.valueOf("1", int.class), should.equal(1));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceAndTargetTypesAreTheSame() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Integer.class, Integer.class);
                }
            }, should.raise(IllegalArgumentException.class));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASubclassOfTheTargetType() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Integer.class, Number.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subclass of java.lang.Number"));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASuperclassOfTheTargetType() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Number.class, Integer.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subclass of java.lang.Number"));
        }
    }
}
