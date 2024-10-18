package sonar.core.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.inventories.SonarInventoryTile;

import java.util.List;

public class TileEntityInventory extends TileEntitySonar implements ISonarInventoryTile {

	public final SonarInventoryTile inv = new SonarInventoryTile(this);
	{
		syncList.addPart(inv);
	}

	public TileEntityInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ISonarInventory inv() {
		return inv;
	}

	public List<ItemStack> slots() {
		return inv.slots();
	}
}
