package sonar.core.api;

import sonar.core.api.nbt.INBTSyncable;

/**
 * Interface representing a Sonar stack, which can be items, energy, or fluids.
 *
 * @param <T> The type of stack extending ISonarStack.
 */
public interface ISonarStack<T extends ISonarStack> extends INBTSyncable {

    /**
     * Enum representing storage types such as items, energy, and fluids.
     */
    enum StorageTypes {
        ITEMS, ENERGY, FLUIDS
    }

    /**
     * @return The storage type for this stack.
     */
    StorageTypes getStorageType();

    /**
     * @return A copy of this stack.
     */
    T copy();

    /**
     * Adds another stack to this stack.
     *
     * @param stack The stack to add.
     */
    void add(T stack);

    /**
     * Removes a stack from this stack.
     *
     * @param stack The stack to remove.
     */
    void remove(T stack);

    /**
     * Sets the stack size.
     *
     * @param size The new size.
     * @return The updated stack.
     */
    T setStackSize(long size);

    /**
     * @return The current stack size.
     */
    long getStackSize();
}
