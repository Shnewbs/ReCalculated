package sonar.core.handlers.inventories.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.TileSonarMultipart;
import sonar.core.network.PacketMultipartSync;

import javax.annotation.Nonnull;

public class ContainerMultipartSync extends Container {

	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };
	public TileSonarMultipart multipart;

	public ContainerMultipartSync(TileSonarMultipart multipart) {
		this.multipart = multipart;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()) {
			super.detectAndSendChanges();
		}
		if (multipart != null && this.listeners != null) {
			CompoundNBT syncData = new CompoundNBT();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				multipart.writeData(syncData, type);
				if (!syncData.isEmpty()) {
					for (IContainerListener listener : listeners) {
						if (listener instanceof ServerPlayerEntity) {
							SonarCore.network.sendTo(new PacketMultipartSync(multipart.getPos(), syncData, type, multipart.getSlotID()), (ServerPlayerEntity) listener);
						}
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotID) {
		return ItemStack.EMPTY;
	}

	public SyncType[] getSyncTypes() {
		return types;
	}

	public boolean syncInventory() {
		return true;
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
		return true;
	}

	public ContainerMultipartSync setTypes(SyncType[] syncTypes) {
		this.types = syncTypes;
		return this;
	}
}
