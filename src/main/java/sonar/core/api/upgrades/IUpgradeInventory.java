package sonar.core.api.upgrades;

import java.util.ArrayList;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import sonar.core.api.nbt.INBTSyncable;

/**
 * Interface for managing upgrades in a TileEntity, allowing addition, removal, and tracking of installed upgrades.
 */
public interface IUpgradeInventory extends INBTSyncable {

    /**
     * Adds an upgrade to the inventory.
     *
     * @param stack The ItemStack representing the upgrade.
     * @return True if the upgrade was successfully added.
     */
    boolean addUpgrade(ItemStack stack);

    /**
     * Removes a specific upgrade from the inventory.
     *
     * @param type The type of upgrade to remove.
     * @param amount The number of upgrades to remove.
     * @return The removed upgrade ItemStack.
     */
    ItemStack removeUpgrade(String type, int amount);

    /**
     * Gets the number of upgrades of a specific type that are installed.
     *
     * @param upgrade The upgrade type.
     * @return The number of upgrades installed.
     */
    int getUpgradesInstalled(String upgrade);

    /**
     * @return A list of allowed upgrades.
     */
    ArrayList<String> getAllowedUpgrades();

    /**
     * @return A map of installed upgrades and their counts.
     */
    THashMap<String, Integer> getInstalledUpgrades();
}
