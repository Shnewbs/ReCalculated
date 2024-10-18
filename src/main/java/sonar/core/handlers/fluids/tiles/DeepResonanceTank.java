package sonar.core.handlers.fluids.tiles;

import java.util.List;

import mcjty.deepresonance.blocks.tank.TileTank;
import mcjty.deepresonance.tanks.TankGrid;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.fluids.FluidHelper;

@FluidHandler(modid = "deepresonance", priority = -1)
public class DeepResonanceTank implements ISonarFluidHandler {
    @Override
    public boolean canHandleFluids(BlockEntity tile, Direction dir) {
        return tile instanceof TileTank;
    }

    @Override
    public StoredFluidStack addStack(StoredFluidStack add, BlockEntity tile, Direction dir, ActionType action) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.addStack(add, tanks, dir, action);
        }
        return add;
    }

    @Override
    public StoredFluidStack removeStack(StoredFluidStack remove, BlockEntity tile, Direction dir, ActionType action) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.removeStack(remove, tanks, dir, action);
        }
        return remove;
    }

    @Override
    public StorageSize getFluids(List<StoredFluidStack> fluids, BlockEntity tile, Direction dir) {
        TileTank tank = (TileTank) tile;
        TankGrid tanks = tank.getTank();
        if (tanks != null) {
            return FluidHelper.getFluids(fluids, tanks, dir);
        }
        return new StorageSize(0, 0);
    }
}
