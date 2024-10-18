package sonar.core.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

/**
 * Interface for flexible GUIs, providing server and client element creation.
 *
 * @param <T> The type of object, which can be a TileEntity, Multipart, or ItemStack.
 */
public interface IFlexibleGui<T> {

	/**
	 * Called when the GUI is opened.
	 *
	 * @param obj The object associated with the GUI (TileEntity, Multipart, or ItemStack).
	 * @param id The GUI ID.
	 * @param world The world instance.
	 * @param player The player instance.
	 * @param tag The NBT tag.
	 */
	default void onGuiOpened(T obj, int id, World world, PlayerEntity player, CompoundNBT tag) {}

	/**
	 * Gets the server element for the GUI.
	 *
	 * @param obj The object (TileEntity, Multipart, or ItemStack).
	 * @param id The GUI ID.
	 * @param world The world instance.
	 * @param player The player instance.
	 * @param tag The NBT tag.
	 * @return The server element.
	 */
	Object getServerElement(T obj, int id, World world, PlayerEntity player, CompoundNBT tag);

	/**
	 * Gets the client element for the GUI.
	 *
	 * @param obj The object (TileEntity, Multipart, or ItemStack).
	 * @param id The GUI ID.
	 * @param world The world instance.
	 * @param player The player instance.
	 * @param tag The NBT tag.
	 * @return The client element.
	 */
	Object getClientElement(T obj, int id, World world, PlayerEntity player, CompoundNBT tag);
}
