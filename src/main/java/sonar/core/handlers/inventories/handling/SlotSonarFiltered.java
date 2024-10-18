package sonar.core.handlers.inventories.handling;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.ISonarInventoryTile;

public class SlotSonarFiltered extends Slot {

    public ISonarInventoryTile invTile;

    public SlotSonarFiltered(ISonarInventoryTile invTile, int index, int xPosition, int yPosition) {
        super(invTile.inv(), index, xPosition, yPosition);  // Pass inventory to the Slot constructor
        this.invTile = invTile;
    }

    @Override
    public int getSlotStackLimit() {
        return invTile.inv().getSlotLimit(getSlotIndex());
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return invTile.inv().checkInsert(getSlotIndex(), stack, null, EnumFilterType.INTERNAL) && super.isItemValid(stack);
    }
}
