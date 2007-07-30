package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
@RunWith(JDaveRunner.class)
public class CommandExecuterSpec extends Specification<Object> {

    public class TargetWithNoParameters {

        private class TargetMock {
            public int fooExecuted = 0;

            public void foo() {
                fooExecuted++;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldCallTheMethodWithTheRightName() throws CommandNotFoundException {
            exec.execute("foo");
            specify(target.fooExecuted, should.equal(1));
        }

        public void wrongCommandShouldDoNothingAndRaiseAnException() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("bar");
                }
            }, should.raise(CommandNotFoundException.class, "bar"));
            specify(target.fooExecuted, should.equal(0));
        }
    }

    public class TargetWithStringParameters {

        private class TargetMock {
            public String fooParameter;
            public String barParameter1;
            public String barParameter2;

            public void foo(String x) {
                fooParameter = x;
            }

            public void bar(String y, String z) {
                barParameter1 = y;
                barParameter2 = z;
            }
        }

        private TargetMock target;

        public Object create() throws CommandNotFoundException {
            target = new TargetMock();
            CommandExecuter exec = new CommandExecuter(target);
            exec.execute("foo x");
            exec.execute("bar y z");
            return null;
        }

        public void shouldPassOnTheParameter() {
            specify(target.fooParameter, should.equal("x"));
        }

        public void shouldPassOnMultipleParameters() {
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }
    }

    public class TargetWithNumericParameters {

        private class TargetMock {
            public Integer integerValue;
            private Double doubleValue;
            public int intValue;

            public void integer(Integer x) {
                integerValue = x;
            }

            public void double_(Double x) {
                doubleValue = x;
            }

            public void int_(int x) {
                intValue = x;
            }
        }

        private TargetMock target;

        public Object create() throws CommandNotFoundException {
            target = new TargetMock();
            CommandExecuter exec = new CommandExecuter(target);
            exec.execute("integer 1");
            exec.execute("double_ 2.5");
            exec.execute("int_ 3");
            return null;
        }

        public void shouldConvertStringsToIntegers() {
            specify(target.integerValue, should.equal(1));
        }

        public void shouldConvertStringsToDoubles() {
            specify(target.doubleValue, should.equal(2.5));
        }

        public void shouldConvertStringsToPrimitives() {
            specify(target.intValue, should.equal(3));
        }
    }
}

