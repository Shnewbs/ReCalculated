package sonar.core.handlers.inventories.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.handlers.inventories.slots.SlotLimiter;

import javax.annotation.Nonnull;

public class ContainerSonar extends Container {

	public void addInventory(InventoryPlayer inventory, int xPos, int yPos) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, xPos + j * 18, yPos + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, xPos + i * 18, yPos + 58));
		}
	}

	public void addInventoryWithLimiter(InventoryPlayer inventory, int xPos, int yPos, Item item) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotLimiter(inventory, j + i * 9 + 9, xPos + j * 18, yPos + i * 18, item));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotLimiter(inventory, i, xPos + i * 18, yPos + 58, item));
		}
	}

	public boolean mergeSonarStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		return this.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
		return true;
	}
}
