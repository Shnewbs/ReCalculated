package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.ArrayList;

/**
 * For use with objects which implement INBTSyncable and have an Empty Constructor for instances
 */
public class SyncTagTypeList<T> extends SyncPart {

	public ArrayList<T> objs = new ArrayList<>();
	private final int nbtType;

	public SyncTagTypeList(int nbtType, int id) {
		super(id);
		this.nbtType = nbtType;
	}

	public SyncTagTypeList(int nbtType, String name) {
		super(name);
		this.nbtType = nbtType;
	}

	public ArrayList<T> getObjects() {
		return objs;
	}

	public void setObjects(ArrayList<T> list) {
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
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName())) {
			NBTTagList tagList = nbt.getTagList(getTagName(), Constants.NBT.TAG_COMPOUND);
			ArrayList<T> newObjs = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound tag = tagList.getCompoundTagAt(i);
				newObjs.add(NBTHelper.readNBTBase(tag, nbtType, getTagName()));
			}
			objs = newObjs;
		} else if (nbt.getBoolean(getTagName() + 'E')) {
			objs = new ArrayList<>();
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagList tagList = new NBTTagList();
		for (T obj : objs) {
			if (obj != null) {
				NBTTagCompound tag = new NBTTagCompound();
				NBTHelper.writeNBTBase(tag, nbtType, obj, getTagName());
				tagList.appendTag(tag);
			}
		}
		if (!tagList.hasNoTags()) {
			nbt.setTag(getTagName(), tagList);
		} else {
			nbt.setBoolean(getTagName() + 'E', true);
		}
		return nbt;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SyncTagTypeList && ((SyncTagTypeList<?>) obj).getObjects().equals(this.objs);
	}
}
