package sonar.core.api.inventories;

import net.minecraft.item.ItemStack;
import sonar.core.common.item.InventoryItem;

/**
 * Interface for handling inventories stored within item stacks.
 */
public interface IItemInventory {

    /**
     * Retrieves the inventory associated with the given item stack.
     *
     * @param stack The item stack to get the inventory from.
     * @return The InventoryItem associated with the stack.
     */
    InventoryItem getInventory(ItemStack stack);
}
