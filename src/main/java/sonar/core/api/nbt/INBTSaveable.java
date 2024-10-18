package sonar.core.api.nbt;

import net.minecraft.nbt.CompoundNBT;

/**
 * Interface for objects that can be saved to and loaded from NBT.
 */
public interface INBTSaveable {

    /**
     * Writes the object data to an NBT tag.
     *
     * @param tag The NBT tag to write data to.
     */
    void writeToNBT(CompoundNBT tag);

    /**
     * Reads the object data from an NBT tag.
     *
     * @param tag The NBT tag to read data from.
     */
    void readFromNBT(CompoundNBT tag);
}
