package sonar.core.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.inventories.SonarLargeInventory;
import sonar.core.handlers.inventories.SonarLargeInventoryTile;

public class TileEntityLargeInventory extends TileEntitySonar implements ISonarInventoryTile {
	public final SonarLargeInventoryTile inv = new SonarLargeInventoryTile(this);

	public TileEntityLargeInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileEntityLargeInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, int size, int stackSize) {
		super(type, pos, state);
		inv.setSize(size);
		inv.setStackSize(stackSize);
		syncList.addPart(inv);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return (T) inv.getItemHandler(facing);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public SonarLargeInventory inv() {
		return inv;
	}
}
