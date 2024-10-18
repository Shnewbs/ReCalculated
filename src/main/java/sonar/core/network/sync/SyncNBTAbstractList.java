package sonar.core.network.sync;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import net.minecraft.nbt.ListNBT; // Updated to use ListNBT
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ByteBufUtils; // Updated import for ByteBufUtils
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

/**
 * For use with objects which implement INBTSyncable and have an empty constructor for instances
 */
public class SyncNBTAbstractList<T extends INBTSyncable> extends SyncPart {

	public List<T> objs = new ArrayList<>();
	public Class<T> type;

	public SyncNBTAbstractList(Class<T> type, int id) {
		super(id);
		this.type = type;
	}

	public SyncNBTAbstractList(Class<T> type, int id, int capacity) {
		super(id);
		this.type = type;
		objs = new ArrayList<>(capacity);
	}

	public List<T> getObjects() {
		return objs;
	}

	public void setObjects(List<T> list) {
		objs = list;
		markChanged();
	}

	public void addObject(T object) {
		if (!objs.contains(object)) {
			objs.add(object);
			markChanged();
		}
	}

	public void removeObject(T object) {
		if (objs.remove(object)) {
			markChanged();
		}
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
			List<T> newObjs = new ArrayList<>();
			ListNBT tagList = nbt.getList(getTagName(), Constants.NBT.TAG_COMPOUND); // Updated to use ListNBT
			for (int i = 0; i < tagList.size(); i++) {
				newObjs.add(NBTHelper.instanceNBTSyncable(this.type, tagList.getCompound(i))); // Updated to use getCompound
			}
			objs = newObjs;
		} else if (nbt.getBoolean(getTagName() + 'E')) {
			objs = new ArrayList<>();
		}
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		ListNBT tagList = new ListNBT(); // Updated to use ListNBT
		objs.forEach(obj -> {
			if (obj != null) {
				tagList.add(obj.writeData(new CompoundNBT(), SyncType.SAVE)); // Updated to use CompoundNBT
			}
		});
		if (!tagList.isEmpty()) { // Updated to use isEmpty
			nbt.put(getTagName(), tagList); // Updated to use put
		} else {
			nbt.putBoolean(getTagName() + 'E', true); // Updated to use putBoolean
		}
		return nbt;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SyncNBTAbstractList && ((SyncNBTAbstractList<?>) obj).getObjects().equals(this.objs);
	}
}
