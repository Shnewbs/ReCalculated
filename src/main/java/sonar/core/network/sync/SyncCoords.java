package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncCoords extends SyncPart {
	private BlockCoords c;

	public SyncCoords(int id) {
		super(id);
	}

	public SyncCoords(String name) {
		super(name);
	}

	public void setCoords(BlockCoords value) {
		if (c == null || !c.equals(value)) {
			c = value;
			this.markChanged();
		}
	}

	public BlockCoords getCoords() {
		return c;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		if (c != null) {
			buf.writeBoolean(true);
			BlockCoords.writeToBuf(buf, c);
		} else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean())
			this.c = BlockCoords.readFromBuf(buf);
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (c != null) {
			CompoundNBT infoTag = new CompoundNBT(); // Updated to use CompoundNBT
			BlockCoords.writeToNBT(infoTag, c);
			nbt.put(this.getTagName(), infoTag); // Updated to use put
		}
		return nbt;
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (nbt.contains(getTagName())) // Updated to use contains
			this.c = BlockCoords.readFromNBT(nbt.getCompound(getTagName())); // Updated to use getCompound
	}
}
