package net.orfjackal.weenyconsole;

/**
 * Converts Strings to objects of another type.
 * Used in connection with {@link CommandExecuter}.
 *
 * @author Esko Luontola
 * @since 2.8.2007
 */
public interface ConstructorFactory<T> {

    Class<T> typeOfCreatedInstances();

    /**
     * @param sourceValue the value to convert. Can not be null.
     * @return a new object instance based on {@code sourceValue}.
     * @throws Exception if conversion is not possible.
     */
    T createNewInstanceFrom(String sourceValue) throws Exception;
}
