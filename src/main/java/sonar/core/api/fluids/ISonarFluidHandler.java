package sonar.core.api.fluids;

import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.utils.ActionType;

/**
 * Interface for handling fluids stored in TileEntities, used by the Fluid Reader.
 * Providers must be registered in {@link SonarAPI} to be utilized.
 */
public interface ISonarFluidHandler {

	/**
	 * Determines if this handler can handle fluids for a given side of the TileEntity.
	 *
	 * @param tile the TileEntity to check.
	 * @param dir the direction to check from.
	 * @return true if the handler can handle fluids on this side.
	 */
	boolean canHandleFluids(TileEntity tile, EnumFacing dir);

	/**
	 * Adds a fluid stack to the fluid inventory.
	 *
	 * @param add the fluid stack to add.
	 * @param tile the TileEntity to add to.
	 * @param dir the direction to check from.
	 * @param action whether the action is simulated or performed.
	 * @return the fluid stack that was not added.
	 */
	StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, EnumFacing dir, ActionType action);

	/**
	 * Removes a fluid stack from the fluid inventory.
	 *
	 * @param remove the fluid stack to remove.
	 * @param tile the TileEntity to remove from.
	 * @param dir the direction to check from.
	 * @param action whether the action is simulated or performed.
	 * @return the fluid stack that was not removed.
	 */
	StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, EnumFacing dir, ActionType action);

	/**
	 * Gets the list of fluids stored in the block and their storage size.
	 *
	 * @param fluids the list of fluids currently in the block.
	 * @param tile the TileEntity to check.
	 * @param dir the direction to check from.
	 * @return the storage size object containing the capacity and stored fluid amounts.
	 */
	StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, EnumFacing dir);
}
