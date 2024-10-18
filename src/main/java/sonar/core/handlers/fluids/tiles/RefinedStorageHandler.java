package sonar.core.handlers.fluids.tiles;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;

import java.util.Collection;
import java.util.List;

@FluidHandler(modid = "refinedstorage", priority = 2)
public class RefinedStorageHandler implements ISonarFluidHandler {

	@Override
	public boolean canHandleFluids(BlockEntity tile, Direction dir) {
		return tile instanceof INetworkNode;
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, BlockEntity tile, Direction dir, ActionType action) {
		INetworkNode node = (INetworkNode) tile;
		INetwork network = node.getNetwork();
		if (network != null) {
			int toAdd = (int) Math.min(Integer.MAX_VALUE, add.stored);
			FluidStack stack = network.insertFluid(add.getFullStack(), toAdd, getAction(action));
			add.stored -= stack == null ? toAdd : toAdd - stack.getAmount();
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, BlockEntity tile, Direction dir, ActionType action) {
		INetworkNode node = (INetworkNode) tile;
		INetwork network = node.getNetwork();
		if (network != null) {
			int toRemove = (int) Math.min(Integer.MAX_VALUE, remove.stored);
			FluidStack stack = network.extractFluid(remove.getFullStack(), toRemove, getAction(action));
			remove.stored -= stack == null ? 0 : stack.getAmount();
		}
		return remove;
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> fluids, BlockEntity tile, Direction dir) {
		INetworkNode node = (INetworkNode) tile;
		INetwork network = node.getNetwork();
		if (network != null) {
			Collection<FluidStack> stacks = network.getFluidStorageCache().getList().getStacks();
			for (FluidStack stack : stacks) {
				SonarAPI.getFluidHelper().addFluidToList(fluids, new StoredFluidStack(stack));
			}
		}
		return new StorageSize(0, 0); // doesn't show storage yet
	}

	public Action getAction(ActionType action) {
		return action == ActionType.PERFORM ? Action.PERFORM : Action.SIMULATE;
	}
}
