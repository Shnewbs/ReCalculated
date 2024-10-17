package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.api.items.IFlawlessCalculator;
import sonar.calculator.mod.common.item.calculators.FlawlessCalculator;
import sonar.calculator.mod.common.tileentity.machines.TileEntityModuleWorkstation;
import sonar.core.handlers.inventories.containers.ContainerSync;
import sonar.core.handlers.inventories.handling.SlotSonarFiltered;

import javax.annotation.Nonnull;

public class ContainerModuleWorkstation extends ContainerSync {
	private final InventoryPlayer inventory;
	private final TileEntityModuleWorkstation entity;

	private static final int INV_START = 17, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	public ContainerModuleWorkstation(InventoryPlayer inventory, TileEntityModuleWorkstation entity) {
		super(entity);
		this.inventory = inventory;
		this.entity = entity;

		for (int j = 0; j < 2; ++j) {
			for (int k = 0; k < 8; ++k) {
				this.addSlotToContainer(new SlotSonarFiltered(entity, k + j * 8, 10 + k * 20, 40 + j * 22));
			}
		}

		addSlotToContainer(new SlotSonarFiltered(entity, FlawlessCalculator.moduleCapacity, 8, 8));
		addInventory(inventory, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUsableByPlayer(player);
	}

    @Nonnull
    @Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotID);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotID < INV_START) {
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else {

                if (itemstack1.getItem() instanceof IFlawlessCalculator) {
                    if (!this.mergeItemStack(itemstack1, 16, 17, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, INV_START - 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
}
