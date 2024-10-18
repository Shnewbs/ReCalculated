package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import net.minecraftforge.common.util.ByteBufUtils; // Updated import for ByteBufUtils
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

/** For use with objects which implement INBTSyncable and have an empty constructor for instances */
public class SyncNBTAbstract<T extends INBTSyncable> extends SyncPart {

	public T obj;
	public Class<T> type;

	public SyncNBTAbstract(Class<T> type, int id) {
		super(id);
		this.type = type;
	}

	public T getObject() {
		return obj;
	}

	public SyncNBTAbstract<T> setObject(T object) {
		obj = object;
		markChanged();
		return this;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new CompoundNBT(), SyncType.SAVE)); // Updated to use CompoundNBT
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (nbt.contains(getTagName())) { // Updated to use contains
			if (isValid(obj)) {
				obj.readData(nbt.getCompound(getTagName()), type); // Updated to use getCompound
			} else {
				obj = NBTHelper.instanceNBTSyncable(this.type, nbt.getCompound(getTagName())); // Updated to use getCompound
			}
		}
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (isValid(obj)) {
			nbt.put(getTagName(), obj.writeData(new CompoundNBT(), type)); // Updated to use put
		}
		return nbt;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SyncNBTAbstract && ((SyncNBTAbstract<?>) obj).obj == this.obj && this.getTagName().equals(((SyncNBTAbstract<?>) obj).getTagName());
	}

	public boolean isValid(T obj) {
		return obj != null;
	}
}
