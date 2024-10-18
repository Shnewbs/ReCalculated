package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncEnum<E extends Enum<E>> extends SyncPart {

	public E[] values;
	public E current;

	public SyncEnum(E[] values, int id) {
		super(id);
		this.values = values;
		this.current = values[0];
	}

	public SyncEnum(E[] values, String name) {
		super(name);
		this.values = values;
		this.current = values[0];
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(current.ordinal());
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		setObject(values[buf.readInt()]);
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (current != null) {
			nbt.putInt(getTagName(), current.ordinal()); // Updated method for writing int
		}
		return nbt;
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (nbt.contains(getTagName())) {
			setObject(values[nbt.getInt(getTagName())]); // Updated method for reading int
		}
	}

	public void incrementEnum() {
		int ordinal = current.ordinal() + 1;
		if (ordinal < values.length) {
			current = values[ordinal];
		} else {
			current = values[0];
		}
		this.markChanged();
	}

	public SyncEnum<E> setDefault(E def) {
		current = def;
		return this;
	}

	public E getObject() {
		return current;
	}

	public void setObject(E object) {
		if (current != object) {
			current = object;
			markChanged();
		}
	}

	@Override
	public String toString() {
		return current.toString();
	}
}
