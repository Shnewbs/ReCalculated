package sonar.core.handlers.fluids.tiles;

import java.util.List;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.fluids.FluidHelper;

@FluidHandler(modid = "sonarcore", priority = 0)
public class FluidCapabilityHandler implements ISonarFluidHandler {

	@Override
	public boolean canHandleFluids(BlockEntity tile, Direction dir) {
		return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).isPresent();
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, BlockEntity tile, Direction dir, ActionType action) {
		return FluidHelper.addStack(add, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).orElse(null), dir, action);
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, BlockEntity tile, Direction dir, ActionType action) {
		return FluidHelper.removeStack(remove, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).orElse(null), dir, action);
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> fluids, BlockEntity tile, Direction dir) {
		return FluidHelper.getFluids(fluids, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir).orElse(null), dir);
	}
}
