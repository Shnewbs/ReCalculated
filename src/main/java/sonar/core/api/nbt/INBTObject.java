package sonar.core.api.nbt;

import sonar.core.api.IRegistryObject;

/**
 * Interface for objects that can be saved to and loaded from NBT, as well as registered.
 *
 * @param <T> The type of object.
 */
public interface INBTObject<T> extends IRegistryObject, INBTSaveable {

    @Override
    String getName();

    /**
     * Returns the instance of the object.
     *
     * @return The instance of the object.
     */
    T instance();
}
