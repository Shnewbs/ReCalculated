package sonar.core.handlers.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import sonar.core.api.energy.EnergyType;

public interface IEnergyTransferProxy {

    double getRFConversion(EnergyType type);

    default boolean isItemEnergyTypeEnabled(EnergyType type) {
        return true;
    }

    default boolean isTileEnergyTypeEnabled(EnergyType type) {
        return true;
    }

    default boolean canConnectTile(BlockEntity tile, Direction face) {
        return true;
    }

    default boolean canConnectItem(ItemStack stack) {
        return true;
    }

    default boolean canConvert(IEnergyHandler to, IEnergyHandler from) {
        return true;
    }

    default boolean canConvert(EnergyType to, EnergyType from) {
        return true;
    }
}
