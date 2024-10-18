package sonar.core.api;

import net.minecraft.nbt.CompoundNBT;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

/**
 * Class representing the storage size for items, energy, or fluids.
 */
public class StorageSize implements INBTSyncable {

	public static final StorageSize EMPTY = new StorageSize(0, 0);

	public long stored, max;

	public StorageSize(long stored, long max) {
		this.stored = stored;
		this.max = max;
	}

	public long getStored() {
		return stored;
	}

	public long getMaxStored() {
		return max;
	}

	public void add(long add) {
		stored += add;
	}

	public void addToMax(long add) {
		max += add;
	}

	public void add(StorageSize size) {
		stored += size.stored;
		max += size.max;
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) {
		stored = nbt.getLong("stored");
		max = nbt.getLong("max");
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) {
		nbt.putLong("stored", stored);
		nbt.putLong("max", max);
		return nbt;
	}
}
