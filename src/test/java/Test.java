import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.util.ClassLoaderRepository;

import java.io.IOException;
import java.util.Arrays;

public class Test {

	public static void main(String[] args) throws IOException {
		JavaClass javaClass;

		// ClassParser parser = new ClassParser("D:\\DOCUMENTS\\PROGRAMS\\OMAT\\weenyconsole\\target\\test-classes\\Test.class");
		// javaClass = parser.parse();

		Repository.setRepository(new ClassLoaderRepository(Test.class
				.getClassLoader()));
		javaClass = Repository.lookupClass(Test.class.getName());

		System.out.println("javaClass = " + javaClass);

		for (Method method : javaClass.getMethods()) {
			System.out.println("----------------");
			LineNumberTable table = method.getLineNumberTable();
			System.out.println("Method = " + method.getName());
			System.out.println("LineNumberTable = "
					+ Arrays.toString(table.getLineNumberTable()));
			System.out.println("Code = " + method.getCode());
		}
	}

	public void empty() {
	}

	// TODO: try to use com.sun.org.apache.bcel.internal.classfile.ClassParser to get the line numbers of the code in Method classes
	// - if it works, fix the sorting of SpecRunner (classes and methods)

	// BCEL Maven: http://mirrors.ibiblio.org/pub/mirrors/maven2/org/apache/bcel/bcel/5.2/
}
