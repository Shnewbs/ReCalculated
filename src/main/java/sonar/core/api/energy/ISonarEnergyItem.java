package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.utils.ActionType;

/**
 * Interface for energy-related operations on items.
 */
public interface ISonarEnergyItem {

    /**
     * Adds energy to the item stack.
     *
     * @param stack The item stack to add energy to.
     * @param maxReceive The maximum amount of energy to add.
     * @param action The action type (simulate or perform).
     * @return The amount of energy actually added.
     */
    long addEnergy(ItemStack stack, long maxReceive, ActionType action);

    /**
     * Removes energy from the item stack.
     *
     * @param stack The item stack to remove energy from.
     * @param maxExtract The maximum amount of energy to remove.
     * @param action The action type (simulate or perform).
     * @return The amount of energy actually removed.
     */
    long removeEnergy(ItemStack stack, long maxExtract, ActionType action);

    /**
     * @return The current energy level of the item stack.
     */
    long getEnergyLevel(ItemStack stack);

    /**
     * @return The maximum energy capacity of the item stack.
     */
    long getFullCapacity(ItemStack stack);
}
