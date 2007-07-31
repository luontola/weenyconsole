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

            public void foo(String s) {
                fooParameter = s;
            }

            public void bar(String s1, String s2) {
                barParameter1 = s1;
                barParameter2 = s2;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportTheUseOfAParameter() throws CommandNotFoundException {
            exec.execute("foo x");
            specify(target.fooParameter, should.equal("x"));
        }

        public void shouldSupportTheUseOfMultipleParameters() throws CommandNotFoundException {
            exec.execute("bar y z");
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }

        public void shouldNotCareAboutEmptySpaceBetweenWords() throws CommandNotFoundException {
            exec.execute(" bar y  z ");
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }

        public void shouldSupportMultipleWordsInDoubleQuotes() throws CommandNotFoundException {
            exec.execute("foo \"two words\"");
            specify(target.fooParameter, should.equal("two words"));
        }

        public void shouldSupportMultipleWordsUsingTheEscapeCharacter() throws CommandNotFoundException {
            exec.execute("foo two\\ words");
            specify(target.fooParameter, should.equal("two words"));
        }

        public void shouldSupportMixingDoubleQuotesAndTheEscapeCharacter() throws CommandNotFoundException {
            exec.execute("foo \"escape char is \\\\ and quote char is \\\"\"");
            specify(target.fooParameter, should.equal("escape char is \\ and quote char is \""));
        }

        public void shouldSupportNewlineCharacter() throws CommandNotFoundException {
            exec.execute("foo line1\\nline2");
            specify(target.fooParameter, should.equal("line1\nline2"));
        }

        public void shouldSupportTabCharacter() throws CommandNotFoundException {
            exec.execute("foo col1\\tcol2");
            specify(target.fooParameter, should.equal("col1\tcol2"));
        }

        public void shouldComplainAboutInvalidUseOfDoubleQuotes() throws CommandNotFoundException {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo \"not properly quoted");
                }
            }, should.raise(IllegalArgumentException.class));
        }

        public void shouldComplainAboutInvalidUseOfTheEscapeCharacter() throws CommandNotFoundException {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo escape_char_has_nothing_to_escape\\");
                }
            }, should.raise(IllegalArgumentException.class));
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
        private CommandExecuter exec;

        public Object create() throws CommandNotFoundException {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportTheUseOfIntegerObjects() throws CommandNotFoundException {
            exec.execute("integer 1");
            specify(target.integerValue, should.equal(1));
        }

        public void shouldSupportTheUseOfDoubleObjects() throws CommandNotFoundException {
            exec.execute("double_ 2.5");
            specify(target.doubleValue, should.equal(2.5));
        }

        public void shouldSupportTheUsePrimitiveTypes() throws CommandNotFoundException {
            exec.execute("int_ 3");
            specify(target.intValue, should.equal(3));
        }
    }

    public class MultiWordCommands {

        private class TargetMock {
            public int fooBarExecuted;

            public void fooBar() {
                fooBarExecuted++;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldCallAMethodWithAllWordsMentioned() throws CommandNotFoundException {
            exec.execute("foo bar");
            specify(target.fooBarExecuted, should.equal(1));
        }
    }
}

