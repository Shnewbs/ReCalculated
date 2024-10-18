package sonar.core.api.nbt;

import net.minecraft.nbt.CompoundNBT;
import sonar.core.helpers.NBTHelper.SyncType;

/**
 * Interface for syncing objects with NBT data.
 * Ensure an empty constructor if initialization is required.
 */
public interface INBTSyncable {

    /**
     * Reads the data from the given NBT tag based on the sync type.
     *
     * @param nbt  The NBT tag to read data from.
     * @param type The sync type used to identify the data being synced.
     */
    void readData(CompoundNBT nbt, SyncType type);

    /**
     * Writes the data to the given NBT tag based on the sync type.
     *
     * @param nbt  The NBT tag to write data to.
     * @param type The sync type used to identify the data being synced.
     * @return The modified NBT tag.
     */
    CompoundNBT writeData(CompoundNBT nbt, SyncType type);
}
