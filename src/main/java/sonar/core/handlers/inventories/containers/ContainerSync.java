package sonar.core.handlers.inventories.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.IWorldPosition;

public class ContainerSync extends ContainerSonar {

	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };
	public INBTSyncable sync;
	public IWorldPosition tile;

	public ContainerSync(INBTSyncable sync, IWorldPosition tile) {
		this.sync = sync;
		this.tile = tile;
	}

	public ContainerSync(TileEntitySonar tile) {
		if (tile instanceof INBTSyncable) {
			sync = tile;
		}
		this.tile = tile;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()) {
			super.detectAndSendChanges();
		}
		if (sync != null && this.listeners != null) {
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				CompoundNBT syncData = sync.writeData(new CompoundNBT(), type);
				if (!syncData.isEmpty()) {
					sendPacketToListeners(new PacketTileSync(tile.getCoords().getBlockPos(), syncData, type));
				}
			}
		}
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		if (listener instanceof ServerPlayerEntity) {
			CompoundNBT saveData = sync.writeData(new CompoundNBT(), SyncType.SAVE);
			if (!saveData.isEmpty()) {
				SonarCore.network.sendTo(new PacketTileSync(tile.getCoords().getBlockPos(), saveData, SyncType.SAVE), (ServerPlayerEntity) listener);
			}
		}
	}

	public final void sendPacketToListeners(IMessage packet) {
		for (IContainerListener o : listeners) {
			if (o instanceof ServerPlayerEntity) {
				SonarCore.network.sendTo(packet, (ServerPlayerEntity) o);
			}
		}
	}

	public SyncType[] getSyncTypes() {
		return types;
	}

	public ContainerSync setTypes(SyncType[] types) {
		this.types = types;
		return this;
	}

	public boolean syncInventory() {
		return true;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return !(tile instanceof IInventory) || ((IInventory) tile).isUsableByPlayer(player);
	}
}
