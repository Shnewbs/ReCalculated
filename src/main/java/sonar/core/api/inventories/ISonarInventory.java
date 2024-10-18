package sonar.core.api.inventories;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.IInsertFilter;
import sonar.core.network.sync.ISyncPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a Sonar inventory, providing support for
 * item handling, filters, and synchronization with NBT.
 */
public interface ISonarInventory extends INBTSyncable, IItemHandlerModifiable, ISyncPart {

	/**
	 * For ISidedInventory.
	 *
	 * @return An array of default slots.
	 */
	int[] getDefaultSlots();

	/**
	 * Gets the item handler for a given side.
	 *
	 * @param side The side to get the handler for.
	 * @return The item handler for the specified side.
	 */
	IItemHandlerModifiable getItemHandler(EnumFacing side);

	/**
	 * @return The wrapper inventory for this Sonar inventory.
	 */
	IInventory getWrapperInventory();

	/**
	 * @return A map of insert filters and their types.
	 */
	Map<IInsertFilter, EnumFilterType> getInsertFilters();

	/**
	 * @return A map of extract filters and their types.
	 */
	Map<IExtractFilter, EnumFilterType> getExtractFilters();

	/**
	 * Checks if an item can be inserted into the slot.
	 *
	 * @param slot The slot to check.
	 * @param stack The item stack to insert.
	 * @param face The face to check from (nullable).
	 * @param internal The filter type to check.
	 * @return True if the item can be inserted.
	 */
	boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType internal);

	/**
	 * Checks if an item can be extracted from the slot.
	 *
	 * @param slot The slot to check.
	 * @param count The amount to extract.
	 * @param face The face to check from (nullable).
	 * @param internal The filter type to check.
	 * @return True if the item can be extracted.
	 */
	boolean checkExtract(int slot, int count, @Nullable EnumFacing face, EnumFilterType internal);

	/**
	 * Checks if an item can be dropped.
	 *
	 * @param slot The slot to check.
	 * @param stack The item stack to drop.
	 * @return True if the item can be dropped.
	 */
	boolean checkDrop(int slot, @Nonnull ItemStack stack);

	/**
	 * @return A list of item stacks that are set to be dropped.
	 */
	List<ItemStack> getDrops();
}
