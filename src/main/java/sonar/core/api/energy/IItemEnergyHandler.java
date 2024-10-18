package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.utils.ActionType;

/**
 * Interface used by Flux Networks to handle energy transfer for items.
 */
public interface IItemEnergyHandler {

	/**
	 * @return The energy type this handler deals with.
	 */
	EnergyType getEnergyType();

	/**
	 * Determines if this handler can add energy to the given item stack.
	 *
	 * @param stack The item stack to check.
	 * @return true if energy can be added, false otherwise.
	 */
	boolean canAddEnergy(ItemStack stack);

	/**
	 * Determines if this handler can remove energy from the given item stack.
	 *
	 * @param stack The item stack to check.
	 * @return true if energy can be removed, false otherwise.
	 */
	boolean canRemoveEnergy(ItemStack stack);

	/**
	 * Determines if this handler can read the energy stored in the given item stack.
	 *
	 * @param stack The item stack to check.
	 * @return true if the energy can be read, false otherwise.
	 */
	boolean canReadEnergy(ItemStack stack);

	/**
	 * Adds energy to the item stack based on the action type.
	 *
	 * @param add The amount of energy to add.
	 * @param stack The item stack to add energy to.
	 * @param actionType The type of action (simulate or perform).
	 * @return The amount of energy actually added.
	 */
	long addEnergy(long add, ItemStack stack, ActionType actionType);

	/**
	 * Removes energy from the item stack based on the action type.
	 *
	 * @param remove The amount of energy to remove.
	 * @param stack The item stack to remove energy from.
	 * @param actionType The type of action (simulate or perform).
	 * @return The amount of energy actually removed.
	 */
	long removeEnergy(long remove, ItemStack stack, ActionType actionType);

	/**
	 * @return The amount of energy currently stored in the item stack.
	 */
	long getStored(ItemStack stack);

	/**
	 * @return The maximum amount of energy that can be stored in the item stack.
	 */
	long getCapacity(ItemStack stack);
}
