package net.orfjackal.weenyconsole;

/**
 * @author Esko Luontola
 * @since 2.8.2007
 */
public interface ConstructorFactory {

    boolean canCreateInstancesOf(Class<?> type);

    Object createNewInstanceFrom(String sourceValue);
}
