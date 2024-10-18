package sonar.core.api.inventories;

import net.minecraft.item.ItemStack;

/**
 * Interface implemented on TileEntities which drop additional items,
 * such as Speed Upgrades or Energy Upgrades.
 */
public interface IAdditionalInventory {

    /**
     * @return An array of additional item stacks that this TileEntity contains.
     */
    ItemStack[] getAdditionalStacks();
}
