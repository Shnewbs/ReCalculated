package sonar.calculator.mod.utils;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.generators.TileEntityCalculatorLocator;

public class SlotLocatorModule extends Slot {

	public TileEntityCalculatorLocator locator;

	public SlotLocatorModule(TileEntityCalculatorLocator locator, int index, int x, int y) {
		super(locator, index, x, y);
		this.locator = locator;
	}

    @Override
	public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Calculator.itemLocatorModule;
	}

    @Override
	public void onSlotChanged() {
		super.onSlotChanged();
		locator.createOwner();
	}
}
