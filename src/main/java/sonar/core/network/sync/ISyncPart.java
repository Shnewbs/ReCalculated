package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public interface ISyncPart extends IDirtyPart, INBTSyncable {

	default void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.writeData(new CompoundNBT(), SyncType.SAVE)); // Updated to use CompoundNBT
	}

	default void readFromBuf(ByteBuf buf){
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	boolean canSync(SyncType sync);

	String getTagName();
}
