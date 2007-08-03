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
@RunWith(JDaveRunner.class)
public class DelegatingConverterSpec extends Specification<DelegatingConverter> {

    public class ADelegatingConverter {

        private Converter realConverter;
        private ConverterProvider provider;

        public DelegatingConverter create() {
            provider = new ConverterProvider();
            realConverter = mock(Converter.class, "realConverter");
            checking(new Expectations(){{
                one(realConverter).supportedTargetType(); will(returnValue(Integer.class));
                one(realConverter).setProvider(provider);
            }});
            provider.addConverter(realConverter);
            return null;
        }

        public void shouldDelegateConversionsOfOneTypeToTheConverterOfAnotherType() throws TargetTypeNotSupportedException, InvalidSourceValueException {
            DelegatingConverter delegator = new DelegatingConverter(int.class, Integer.class);
            provider.addConverter(delegator);
            checking(new Expectations(){{
                one(realConverter).valueOf("1", Integer.class); will(returnValue(1));
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

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASubclassOfTheTargetTarget() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Integer.class, Number.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subtype of java.lang.Number"));
        }

        public void shouldNotAllowAnInfiniteLoopWhenSourceIsASuperclassOfTheTargetTarget() {
            specify(new Block() {
                public void run() throws Throwable {
                    new DelegatingConverter(Number.class, Integer.class);
                }
            }, should.raise(IllegalArgumentException.class, "java.lang.Integer is a subtype of java.lang.Number"));
        }
    }
}
