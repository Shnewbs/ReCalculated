package sonar.core.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public class TileEntityEnergySidedInventory extends TileEntityEnergyInventory implements IMachineSides, WorldlyContainer {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);

	public TileEntityEnergySidedInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		super.inv.getInsertFilters().put(sides, EnumFilterType.EXTERNAL);
		super.inv.getExtractFilters().put(sides, EnumFilterType.EXTERNAL);
		syncList.addPart(sides);
	}

	@Override
	public MachineSides getSideConfigs() {
		return sides;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		// Add logic to return available slots for the given side
		return new int[0];
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		// Add logic to determine if an item can be inserted through the given side
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		// Add logic to determine if an item can be extracted through the given side
		return true;
	}
}
