package sonar.core.network.sync;

import net.minecraft.nbt.CompoundNBT;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public abstract class BaseSyncListPart extends DirtyPart implements ISyncableListener, INBTSyncable {

	public SyncableList syncList = new SyncableList(this);

	public BaseSyncListPart() {
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) {
		if (shouldEmbed()) {
			CompoundNBT tag = nbt.getCompound(((ISyncPart) this).getTagName());
			if (!tag.isEmpty()) {
				NBTHelper.readSyncParts(tag, type, syncList);
			}
		} else {
			NBTHelper.readSyncParts(nbt, type, syncList);
		}
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) {
		if (shouldEmbed()) {
			CompoundNBT tag = NBTHelper.writeSyncParts(new CompoundNBT(), type, syncList, type == SyncType.SYNC_OVERRIDE);
			if (!tag.isEmpty()) {
				nbt.put(((ISyncPart) this).getTagName(), tag);
			}
		} else {
			NBTHelper.writeSyncParts(nbt, type, syncList, type.isType(SyncType.SAVE));
		}
		return nbt;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncList.markSyncPartChanged(part);
		markDirty(); // Updated to use the new method name in NeoForge
	}

	public boolean shouldEmbed() {
		return this instanceof ISyncPart;
	}
}
