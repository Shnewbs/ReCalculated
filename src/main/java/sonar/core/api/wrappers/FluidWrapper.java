package sonar.core.api.wrappers;

import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.fluids.FluidHelper.ITankFilter;

/**
 * A wrapper for handling fluid interactions, such as adding and removing fluids from tanks.
 */
public class FluidWrapper {

	/**
	 * Convenience method, adds the given fluid stack to the list, used by {@link FluidHandler}.
	 *
	 * @param list  The list of {@link StoredFluidStack} to add to.
	 * @param stack The {@link StoredFluidStack} to combine.
	 */
	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {
	}

	/**
	 * Convenience method, creates a {@link StoredFluidStack} for how much should be added.
	 *
	 * @param inputSize The size of input.
	 * @param stack     The fluid stack to add.
	 * @param returned  The amount returned.
	 * @return A {@link StoredFluidStack} representing the amount to add.
	 */
	public StoredFluidStack getStackToAdd(long inputSize, StoredFluidStack stack, StoredFluidStack returned) {
		return null;
	}

	/**
	 * Adds fluids to a tile entity.
	 *
	 * @param add    The fluid stack to add.
	 * @param tile   The tile entity to add to.
	 * @param face   The side to add from.
	 * @param type   The action type (simulate or perform).
	 * @param filter The tank filter.
	 * @return The remaining fluid after the addition.
	 */
	public StoredFluidStack addFluids(StoredFluidStack add, TileEntity tile, Direction face, ActionType type, ITankFilter filter) {
		return null;
	}

	/**
	 * Removes fluids from a tile entity.
	 *
	 * @param remove The fluid stack to remove.
	 * @param tile   The tile entity to remove from.
	 * @param face   The side to remove from.
	 * @param type   The action type (simulate or perform).
	 * @param filter The tank filter.
	 * @return The remaining fluid after removal.
	 */
	public StoredFluidStack removeFluids(StoredFluidStack remove, TileEntity tile, Direction face, ActionType type, ITankFilter filter) {
		return null;
	}

	/**
	 * Transfers fluids between two tile entities.
	 *
	 * @param from     The source tile entity.
	 * @param to       The destination tile entity.
	 * @param dirFrom  The direction to extract from.
	 * @param dirTo    The direction to insert into.
	 * @param filter   The tank filter.
	 */
	public void transferFluids(TileEntity from, TileEntity to, Direction dirFrom, Direction dirTo, ITankFilter filter) {
	}
}
