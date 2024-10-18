package sonar.core.api;

/**
 * Interface for registry objects that can be checked for loadability and have a name.
 */
public interface IRegistryObject {

    /**
     * Checks if the object is loadable.
     *
     * @return True if the object is loadable, false otherwise.
     */
    boolean isLoadable();

    /**
     * Gets the name of the object.
     *
     * @return The name of the object.
     */
    String getName();
}
