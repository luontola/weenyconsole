package net.orfjackal.weenyconsole;

import jdave.Block;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

import java.awt.*;

/**
 * @author Esko Luontola
 * @since 31.7.2007
 */
@RunWith(JDaveRunner.class)
public class CommandExecuterSpec extends Specification<Object> {

    public class CommandsWithNoParameters {

        private class TargetMock implements CommandService {
            private int fooExecuted = 0;

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

        public void shouldCallAMethodWithTheSameNameAsTheCommand() {
            exec.execute("foo");
            specify(target.fooExecuted, should.equal(1));
        }

        public void shouldRaiseAnExceptionOnAnUnknownCommand() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("bar");
                }
            }, should.raise(CommandNotFoundException.class, "command not found: bar"));
            specify(target.fooExecuted, should.equal(0));
        }

        public void shouldExitSilentlyOnAnEmptyCommand() {
            exec.execute("");               // trivial case of an empty string
            exec.execute("  \n");           // whitespace should also be regarded as an empty command
            specify(target.fooExecuted, should.equal(0));
        }
    }

    public class CommandsWithStringParameters {

        private class TargetMock implements CommandService {
            private String fooParameter;
            private String barParameter1;
            private String barParameter2;

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

        public void shouldSupportAParameter() {
            exec.execute("foo x");
            specify(target.fooParameter, should.equal("x"));
        }

        public void shouldSupportMultipleParameters() {
            exec.execute("bar y z");
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }

        public void shouldNotAllowUsingTooFewParameters() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo");
                }
            }, should.raise(CommandNotFoundException.class));
        }

        public void shouldNotAllowUsingTooManyParameters() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo too many");
                }
            }, should.raise(CommandNotFoundException.class));
        }

        public void shouldNotCareAboutEmptySpaceBetweenParameters() {
            exec.execute(" bar y  z ");
            specify(target.barParameter1, should.equal("y"));
            specify(target.barParameter2, should.equal("z"));
        }

        public void shouldSupportMultipleWordsPerParameterInDoubleQuotes() {
            exec.execute("foo \"two words\"");
            specify(target.fooParameter, should.equal("two words"));
        }

        public void shouldSupportMultipleWordsPerParameterUsingEscapeSequences() {
            exec.execute("foo two\\ words");
            specify(target.fooParameter, should.equal("two words"));
        }

        public void shouldSupportMixingDoubleQuotesAndEscapeSequences() {
            exec.execute("foo \"escape char is \\\\ and\\ quote char is \\\"\"");
            specify(target.fooParameter, should.equal("escape char is \\ and quote char is \""));
        }

        public void shouldSupportTheNewlineCharacter() {
            exec.execute("foo line1\\nline2");
            specify(target.fooParameter, should.equal("line1\nline2"));
        }

        public void shouldSupportTheTabCharacter() {
            exec.execute("foo col1\\tcol2");
            specify(target.fooParameter, should.equal("col1\tcol2"));
        }

        public void shouldSupportNullAsAParameter() {
            target.fooParameter = "initial value";
            exec.execute("foo \\0");
            specify(target.fooParameter, should.equal(null));
        }

        public void shouldNotAllowNullAsAPartOfAWord() {
            target.fooParameter = "initial value";
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo aa\\0aa");
                }
            }, should.raise(MalformedCommandException.class, "" +
                    "null not allowed here: foo aa\\0aa\n" +
                    "                              ^"));
            specify(target.fooParameter, should.equal("initial value"));
        }

        public void shouldComplainAboutMissingDoubleQuotes() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo \"not properly quoted");
                }
            }, should.raise(MalformedCommandException.class, "" +
                    "double quote expected: foo \"not properly quoted\n" +
                    "                                               ^"));
        }

        public void shouldComplainAboutInvalidEscapeSequences() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo \\e_is_not_a_valid_escape_sequence");
                }
            }, should.raise(MalformedCommandException.class, "" +
                    "escape sequence expected: foo \\e_is_not_a_valid_escape_sequence\n" +
                    "                               ^"));
        }

        public void shouldComplainAboutIncompleteEscapeSequences() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("foo escape_char_has_nothing_to_escape\\");
                }
            }, should.raise(MalformedCommandException.class, "" +
                    "escape sequence expected: foo escape_char_has_nothing_to_escape\\\n" +
                    "                                                                ^"));
        }
    }

    public class CommandsWithNumericParameters {

        private class TargetMock implements CommandService {
            private Integer integerValue;
            private Double doubleValue;
            private Boolean primBoolean;
            private Byte primByte;
            private Character primChar;
            private Short primShort;
            private Integer primInt;
            private Long primLong;
            private Float primFloat;
            private Double primDouble;
            private Integer nullCheckValue;
            private Character characterCheckValue;

            public void integer(Integer x) {
                integerValue = x;
            }

            public void double_(Double x) {
                doubleValue = x;
            }

            public void primitives(boolean boolean_, byte byte_, char char_, short short_,
                                   int int_, long long_, float float_, double double_) {
                primBoolean = boolean_;
                primByte = byte_;
                primChar = char_;
                primShort = short_;
                primInt = int_;
                primLong = long_;
                primFloat = float_;
                primDouble = double_;
            }

            public void nullCheck(int x) {
                nullCheckValue = x;
            }

            public void characterCheck(Character x) {
                characterCheckValue = x;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportIntegerObjectsAsAParameter() {
            exec.execute("integer 1");
            specify(target.integerValue, should.equal(1));
        }

        public void shouldSupportDoubleObjectsAsAParameter() {
            exec.execute("double_ 2.5");
            specify(target.doubleValue, should.equal(2.5));
        }

        public void shouldSupportAllPrimitiveTypesAsAParameter() {
            exec.execute("primitives true -128 c 32767 2147483647 9223372036854775807 1.123 2.456");
            specify(target.primBoolean, should.equal(Boolean.TRUE));
            specify(target.primByte, should.equal(Byte.MIN_VALUE));
            specify(target.primChar, should.equal('c'));
            specify(target.primShort, should.equal(Short.MAX_VALUE));
            specify(target.primInt, should.equal(Integer.MAX_VALUE));
            specify(target.primLong, should.equal(Long.MAX_VALUE));
            specify(target.primFloat, should.equal(1.123F));
            specify(target.primDouble, should.equal(2.456));
        }

        public void shouldNotAllowConvertingNullToAPrimitiveType() {
            target.nullCheckValue = 123;
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("nullCheck \\0");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.nullCheckValue, should.equal(123));
        }

        public void shouldNotAllowConvertingManyLettersToACharacterParameter() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("characterCheck ab");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.characterCheckValue, should.equal(null));
        }
    }

    public static class CustomObject {
        private String value;

        public CustomObject(String x) {
            this.value = x;
        }
    }

    public class CommandsWithObjectParameters {

        private class TargetMock implements CommandService {
            private CustomObject constructorParam;
            public Point factoryParam;
            private Integer integerParam;
            private Integer constructorErrorValue;

            public void constructor(CustomObject x) {
                constructorParam = x;
            }

            public void factory(Point x) {
                factoryParam = x;
            }

            public void integer(Integer x) {
                integerParam = x;
            }

            public void constructorError(int x) {
                constructorErrorValue = x;
            }
        }

        private class PointConverter implements Converter {

            public Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
                try {
                    String[] xy = sourceValue.split(",", 2);
                    return new Point(Integer.valueOf(xy[0]), Integer.valueOf(xy[1]));
                } catch (Exception e) {
                    throw new InvalidSourceValueException(sourceValue, targetType, e);
                }
            }

            public Class<Point> supportedTargetType() {
                return Point.class;
            }

            public void setProvider(ConversionService provider) {
            }
        }

        private class DoublingIntegerConverter implements Converter {

            public Object valueOf(String sourceValue, Class<?> targetType) throws InvalidSourceValueException, TargetTypeNotSupportedException {
                return (Integer.valueOf(sourceValue) * 2);
            }

            public Class<?> supportedTargetType() {
                return Integer.class;
            }

            public void setProvider(ConversionService provider) {
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportAnyObjectsWithAStringConstructorAsAParameter() {
            exec.execute("constructor foo");
            specify(target.constructorParam.value, should.equal("foo"));
        }

        public void shouldSupportOtherObjectsAsAParameterWhenAFactoryIsProvided() {
            exec.addConverter(new PointConverter());
            exec.execute("factory 1,2");
            specify(target.factoryParam, should.equal(new Point(1, 2)));
        }

        public void theFactoryShouldTakePriorityOverTheConstructor() {
            exec.addConverter(new DoublingIntegerConverter());
            exec.execute("integer 3");
            specify(target.integerParam, should.equal(6));
        }

        /**
         * The constructor {@link Integer#Integer(String)} will throw
         * an exception if the string can not be converted to an integer.
         * The program must handle silently all exceptions thrown by a constructor.
         */
        public void shouldNotCallMethodsToWhichTheParameterCanNotBeConvertedUsingAConstructor() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("constructorError not_int");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.constructorErrorValue, should.equal(null));
        }

        public void shouldNotCallMethodsToWhichTheParameterCanNotBeConvertedUsingAFactory() {
            exec.addConverter(new PointConverter());
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("factory 1");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.factoryParam, should.equal(null));
        }
    }

    private enum MyEnum {
        FOO, BAR
    }

    public class CommandsWithEnumParameters {

        private class TargetMock implements CommandService {
            private MyEnum enumMethodParam;

            public void enumMethod(MyEnum x) {
                enumMethodParam = x;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportEnumsAsAParameter() {
            exec.execute("enumMethod BAR");
            specify(target.enumMethodParam, should.equal(MyEnum.BAR));
        }

        public void shouldNotAllowInvalidEnumNames() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("enumMethod bar");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.enumMethodParam, should.equal(null));
        }
    }

    public class CommandsWithVarargParameters {

        private class TargetMock implements CommandService {
            private String normalParam;
            private String[] varargParams;

            public void vararg(String... varargs) {
                varargParams = varargs;
            }

            public void mixedVararg(String normal, String... varargs) {
                normalParam = normal;
                varargParams = varargs;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldSupportVarargParameters() {
            exec.execute("vararg one two three");
            specify(target.normalParam, should.equal(null));
            specify(target.varargParams, should.containInOrder("one", "two", "three"));
        }

        public void shouldSupportMixingNormalAndVarargParameters() {
            exec.execute("mixedVararg zero one two three");
            specify(target.normalParam, should.equal("zero"));
            specify(target.varargParams, should.containInOrder("one", "two", "three"));
        }

        public void shouldAllowZeroLengthVarargParameters() {
            exec.execute("vararg");
            specify(target.normalParam, should.equal(null));
            specify(target.varargParams, should.containInOrder());
        }

        public void shouldNotAllowTooFewParameters() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("mixedVararg");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.normalParam, should.equal(null));
            specify(target.varargParams, should.equal(null));
        }
    }

    public class MultiWordCommands {

        private class TargetMock implements CommandService {
            private int methodOneExecuted;
            private int methodOneMoreExecuted;
            private int methodTwoValue;
            private int longLongerExecuted;
            private int longLongerLongestExecuted;

            public void methodOne() {
                methodOneExecuted++;
            }

            public void methodOneMore() {
                methodOneMoreExecuted++;
            }

            public void methodTwo(int x) {
                methodTwoValue = x;
            }

            @SuppressWarnings({"UnusedDeclaration"})
            public void longLonger(String s) {
                longLongerExecuted++;
            }

            public void longLongerLongest() {
                longLongerLongestExecuted++;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldCallAMethodWhoseNameHasAllTheWords() {
            exec.execute("method one");
            specify(target.methodOneExecuted, should.equal(1));
        }

        public void shouldSupportMethodsWithParameters() {
            exec.execute("method two 42");
            specify(target.methodTwoValue, should.equal(42));
        }

        public void shouldSupportOverloadingCommandNames() {
            exec.execute("method one more");
            specify(target.methodOneMoreExecuted, should.equal(1));
        }

        public void shouldPrioritizeTheMethodWithTheLongestName() {
            exec.execute("long longer longest");
            specify(target.longLongerLongestExecuted, should.equal(1));
            specify(target.longLongerExecuted, should.equal(0));
        }
    }

    public class CommandsWithOverloadedMethods {

        private class TargetMock implements CommandService {
            private Integer overloadedInt;
            private Boolean overloadedBoolean;
            private Class<?> ambiguousValue1;
            private Class<?> ambiguousValue2;

            public void overloaded(int x) {
                overloadedInt = x;
            }

            public void overloaded(boolean x) {
                overloadedBoolean = x;
            }

            public void ambiguous(Integer x) {
                ambiguousValue1 = x.getClass();
            }

            public void ambiguous(Double x) {
                ambiguousValue1 = x.getClass();
            }

            public void ambiguous(Float x1, String... x2) {
                ambiguousValue1 = x1.getClass();
                ambiguousValue2 = x2.getClass();
            }

            public void ambiguous(Float x) {
                ambiguousValue1 = x.getClass();
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldChooseFromOverloadedMethodsTheOneToWhichTheParametersCanBeConvertedV1() {
            exec.execute("overloaded 42");
            specify(target.overloadedInt, should.equal(42));
            specify(target.overloadedBoolean, should.equal(null));
        }

        public void shouldChooseFromOverloadedMethodsTheOneToWhichTheParametersCanBeConvertedV2() {
            exec.execute("overloaded true");
            specify(target.overloadedInt, should.equal(null));
            specify(target.overloadedBoolean, should.equal(true));
        }

        public void shouldNotAllowAmbiguousParameters() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("ambiguous 1");
                }
            }, should.raise(AmbiguousMethodsException.class, "" +
                    "   command failed: ambiguous 1\n" +
                    "ambiguous methods: ambiguous(java.lang.Double)\n" + // alphabetical order, shortest first
                    "                   ambiguous(java.lang.Float)\n" +
                    "                   ambiguous(java.lang.Float,[Ljava.lang.String;)\n" +
                    "                   ambiguous(java.lang.Integer)"));
            specify(target.ambiguousValue1, should.equal(null));
            specify(target.ambiguousValue2, should.equal(null));
        }
    }

    public class InACornerSituationTheCommandExecuter {

        private class TargetMock implements CommandService {
            private Boolean booleanCaseValue;
            private int nullCheckExecuted;

            public void booleanCase(boolean x) {
                booleanCaseValue = x;
            }

            public void nullChecknull() {
                nullCheckExecuted++;
            }

            public void nullCheckNull() {
                nullCheckExecuted++;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        /**
         * Boolean's constructor would convert "foo" to FALSE, but
         * we want to have stricter conversions, especially if there
         * would be an overloaded method with more exact parameters.
         */
        public void shouldAllowCreatingABooleanOnlyFromTrueOrFalse() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("booleanCase 0");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.booleanCaseValue, should.equal(null));
            exec.execute("booleanCase true");
            specify(target.booleanCaseValue, should.equal(true));
            exec.execute("booleanCase false");
            specify(target.booleanCaseValue, should.equal(false));
        }

        public void shouldNotMistakeNullValuesToBePartOfTheMethodName() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("null check \\0");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.nullCheckExecuted, should.equal(0));
        }
    }

    public class WhenTheTargetMethodReturnsAValue {

        private class TargetMock implements CommandService {
            private String stringToReturn;

            public String returnsString() {
                return stringToReturn;
            }

            public void voidMethod() {
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldReturnTheValueToTheUser() {
            target.stringToReturn = "the value";
            Object value = exec.execute("returnsString");
            specify(value, should.equal("the value"));
        }

        public void shouldReturnNullFromVoidMethods() {
            Object value = exec.execute("voidMethod");
            specify(value, should.equal(null));
        }
    }

    public class WhenTheTargetMethodThrowsAnException {

        private class TargetMock implements CommandService {
            public void exceptionThrower() {
                throw new IllegalStateException("some exception");
            }
        }

        private CommandExecuter exec;

        public Object create() {
            TargetMock target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void itShouldBeWrappedAndRethrownToTheUser() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("exceptionThrower");
                }
            }, should.raise(CommandTargetException.class, "exception was thrown: " +
                    "java.lang.IllegalStateException: some exception"));
        }
    }

    private static class VisibilityRulesTargetMock implements CommandService {
        public int publicMethodExecuted;
        public int protectedMethodExecuted;
        public int packageMethodExecuted;
        public int privateMethodExecuted;
        private static int staticMethodExecuted;

        public void publicMethod() {
            publicMethodExecuted++;
        }

        protected void protectedMethod() {
            protectedMethodExecuted++;
        }

        void packageMethod() {
            packageMethodExecuted++;
        }

        @SuppressWarnings({"UnusedDeclaration"})
        private void privateMethod() {
            privateMethodExecuted++;
        }

        public static void staticMethod() {
            staticMethodExecuted++;
        }
    }

    public class VisibilityRules {

        private VisibilityRulesTargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new VisibilityRulesTargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldAllowCallingOnlyPublicInstanceMethods() {
            exec.execute("publicMethod");
            specify(target.publicMethodExecuted, should.equal(1));
        }

        public void shouldNotAllowCallingProtectedMethods() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("protectedMethod");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.protectedMethodExecuted, should.equal(0));
        }

        public void shouldNotAllowCallingPackagePrivateMethods() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("packageMethod");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.packageMethodExecuted, should.equal(0));
        }

        public void shouldNotAllowCallingPrivateMethods() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("privateMethod");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.privateMethodExecuted, should.equal(0));
        }

        public void shouldNotAllowCallingStaticMethods() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("staticMethod");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(VisibilityRulesTargetMock.staticMethodExecuted, should.equal(0));
        }
    }

    public class InheritanceRules {

        private class TargetMock extends ParentMock {
        }

        private class ParentMock extends SuperParentMock implements CommandService {
            public int inheritedAllowedMethodExecuted;

            public void inheritedAllowedMethod() {
                inheritedAllowedMethodExecuted++;
            }

            @Override
            public void inheritedOverriddenMethod() {
                super.inheritedOverriddenMethod();
            }
        }

        private class SuperParentMock {
            public int inheritedUnallowedMethodExecuted;
            public int inheritedOverriddenMethodExecuted;

            public void inheritedUnallowedMethod() {
                inheritedUnallowedMethodExecuted++;
            }

            public void inheritedOverriddenMethod() {
                inheritedOverriddenMethodExecuted++;
            }
        }

        private TargetMock target;
        private CommandExecuter exec;

        public Object create() {
            target = new TargetMock();
            exec = new CommandExecuter(target);
            return null;
        }

        public void shouldAllowCallingMethodsInheritedFromClassesWhichImplementTheMarkerInterface() {
            exec.execute("inheritedAllowedMethod");
            specify(target.inheritedAllowedMethodExecuted, should.equal(1));
        }

        public void shouldNotAllowCallingAnyOtherInheritedMethods() {
            specify(new Block() {
                public void run() throws Throwable {
                    exec.execute("inheritedUnallowedMethod");
                }
            }, should.raise(CommandNotFoundException.class));
            specify(target.inheritedUnallowedMethodExecuted, should.equal(0));
        }

        public void shouldAllowCallingInheritedNotMarkedMethodsIfTheyAreOverridedInASubClass() {
            exec.execute("inheritedOverriddenMethod");
            specify(target.inheritedOverriddenMethodExecuted, should.equal(1));
        }
    }

    // TODO: support for array parameters: foo { item item }
    // TODO: support for multidimensional array parameters
    // TODO: should show a help message when using wrong number of parameters
    // TODO: should show a help message when using wrong types of parameters
    // TODO: should show possible values for enums in the help message
}
