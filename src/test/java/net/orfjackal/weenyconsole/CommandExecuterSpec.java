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

    public class CommandsWithNoParameters {

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

        public void shouldCallAMethodWithTheSameNameAsTheCommand() throws CommandNotFoundException {
            exec.execute("foo");
            specify(target.fooExecuted, should.equal(1));
        }

        public void shouldRaiseAnExceptionOnAnUnknownCommand() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("bar");
                }
            }, should.raise(CommandNotFoundException.class, "bar"));
            specify(target.fooExecuted, should.equal(0));
        }
    }

    public class CommandsWithStringParameters {

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

        public void shouldSupportTheUseOfAParameter() {
            specify(target.fooParameter, should.equal("x"));
        }

        public void shouldSupportTheUseOfMultipleParameters() {
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }
    }

    public class CommandsWithNumericParameters {

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

        public void shouldSupportTheUseOfIntegerObjects() {
            specify(target.integerValue, should.equal(1));
        }

        public void shouldSupportTheUseOfDoubleObjects() {
            specify(target.doubleValue, should.equal(2.5));
        }

        public void shouldSupportTheUsePrimitiveTypes() {
            specify(target.intValue, should.equal(3));
        }
    }
}

