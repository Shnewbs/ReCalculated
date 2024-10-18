package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import sonar.core.api.nbt.IBufManager;
import sonar.core.api.nbt.IBufObject;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncGeneric<T extends IBufObject> extends SyncPart {
	private T c;
	private IBufManager<T> manager;

	public SyncGeneric(IBufManager<T> manager, int id) {
		super(id);
		this.manager = manager;
	}

	public SyncGeneric(IBufManager<T> manager, String name) {
		super(name);
		this.manager = manager;
	}

	public void setDefault(T def) {
		this.c = def;
	}

	public void setObject(T value) {
		c = value;
		this.markChanged();
	}

	public T getObject() {
		return c;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		manager.writeToBuf(buf, c);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.c = manager.readFromBuf(buf);
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		CompoundNBT infoTag = new CompoundNBT(); // Updated to use CompoundNBT
		manager.writeToNBT(infoTag, c);
		if (!infoTag.isEmpty()) { // Updated method to check if NBT is empty
			nbt.put(getTagName(), infoTag); // Updated method for setting NBT tag
		}
		return nbt;
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (nbt.contains(getTagName())) { // Updated method for checking NBT tag
			this.c = manager.readFromNBT(nbt.getCompound(getTagName())); // Updated method for getting NBT compound
		}
	}

	@Override
	public String toString() {
		return c != null ? c.toString() : "null"; // Prevents NullPointerException
	}
}
