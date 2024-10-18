package sonar.core.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Handler used by Flux Networks to manage energy transfer for tiles.
 */
public interface ITileEnergyHandler {

	/**
	 * @return The type of energy this handler manages.
	 */
	EnergyType getEnergyType();

	/**
	 * Checks if Flux Networks can render a connection to the tile.
	 *
	 * @param tile The tile entity.
	 * @param dir The side to check (nullable).
	 * @return true if the connection can be rendered.
	 */
	boolean canRenderConnection(@Nonnull TileEntity tile, @Nullable EnumFacing dir);

	/**
	 * Checks if energy can be added to the tile from a specific direction.
	 *
	 * @param tile The tile entity.
	 * @param dir The direction to check.
	 * @return true if energy can be added.
	 */
	boolean canAddEnergy(TileEntity tile, EnumFacing dir);

	/**
	 * Checks if energy can be removed from the tile from a specific direction.
	 *
	 * @param tile The tile entity.
	 * @param dir The direction to check.
	 * @return true if energy can be removed.
	 */
	boolean canRemoveEnergy(TileEntity tile, EnumFacing dir);

	/**
	 * Checks if energy can be read from the tile.
	 *
	 * @param tile The tile entity.
	 * @param dir The direction to check.
	 * @return true if energy can be read.
	 */
	boolean canReadEnergy(TileEntity tile, EnumFacing dir);

	/**
	 * Adds energy to the tile.
	 *
	 * @param add The amount of energy to add.
	 * @param tile The tile entity.
	 * @param dir The direction to add energy from.
	 * @param actionType The action type (simulate or perform).
	 * @return The amount of energy actually added.
	 */
	long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType);

	/**
	 * Removes energy from the tile.
	 *
	 * @param remove The amount of energy to remove.
	 * @param tile The tile entity.
	 * @param dir The direction to remove energy from.
	 * @param actionType The action type (simulate or perform).
	 * @return The amount of energy actually removed.
	 */
	long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType);

	/**
	 * Gets the current energy stored in the tile.
	 *
	 * @param tile The tile entity.
	 * @param dir The direction to check.
	 * @return The amount of energy stored.
	 */
	long getStored(TileEntity tile, EnumFacing dir);

	/**
	 * Gets the maximum energy capacity of the tile.
	 *
	 * @param tile The tile entity.
	 * @param dir The direction to check.
	 * @return The maximum energy capacity.
	 */
	long getCapacity(TileEntity tile, EnumFacing dir);
}
