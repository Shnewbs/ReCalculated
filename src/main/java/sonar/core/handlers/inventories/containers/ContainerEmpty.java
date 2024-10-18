package sonar.core.handlers.inventories.containers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.handlers.inventories.TransferSlotsManager;

import javax.annotation.Nonnull;

public class ContainerEmpty extends ContainerSync {

	public ContainerEmpty(Inventory playerInventory, TileEntitySonar tile) {
		super(tile);
		addInventory(playerInventory, 8, 84);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Nonnull
	@Override
	public final ItemStack quickMoveStack(Player player, int slotID) {
		return TransferSlotsManager.DEFAULT.transferStackInSlot(this, null, player, slotID);
	}
}
