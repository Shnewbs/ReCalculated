package sonar.core.api.nbt;

import net.minecraft.nbt.CompoundNBT;

/**
 * Deprecated interface for managing reading and writing objects to and from NBT.
 *
 * @param <T> The type of object that implements INBTObject.
 */
@Deprecated
public interface INBTManager<T extends INBTObject> {

    /**
     * Reads the object data from an NBT tag.
     *
     * @param tag The NBT tag to read from.
     * @return The object read from the NBT.
     */
    T readFromNBT(CompoundNBT tag);

    /**
     * Writes the object data to an NBT tag.
     *
     * @param tag The NBT tag to write to.
     * @param object The object to write.
     * @return The NBT tag with the written data.
     */
    CompoundNBT writeToNBT(CompoundNBT tag, T object);

    /**
     * Checks if the two objects are of the same type.
     *
     * @param target The target object.
     * @param current The current object.
     * @return True if they are of the same type, false otherwise.
     */
    boolean areTypesEqual(T target, T current);
}
