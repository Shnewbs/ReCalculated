package sonar.calculator.mod.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.common.containers.ICalculatorCrafter;

import javax.annotation.Nonnull;

public class SlotPortableCrafting extends SlotPortable {

	private ICalculatorCrafter container;

	public SlotPortableCrafting(ICalculatorCrafter container, IInventory inv, int index, int x, int y, Item type) {
		super(inv, index, x, y, type);
		this.container = container;
	}

    @Nonnull
    @Override
	public ItemStack decrStackSize(int size) {
		if (!invItem.getStackInSlot(this.slotNumber).isEmpty()) {
			ItemStack itemstack;
			if (invItem.getStackInSlot(this.slotNumber).getCount() <= size) {
				itemstack = invItem.getStackInSlot(this.slotNumber);
				invItem.setInventorySlotContents(this.slotNumber, ItemStack.EMPTY);
				container.onItemCrafted();
				return itemstack;
			} else {
				itemstack = invItem.getStackInSlot(this.slotNumber).splitStack(size);
				container.onItemCrafted();
				return itemstack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void putStack(ItemStack stack) {
		super.putStack(stack);
		container.onItemCrafted();
	}
}
