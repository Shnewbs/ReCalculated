package sonar.core.handlers.inventories;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.IInventoryWrapper;

import javax.annotation.Nonnull;

public class SonarInventoryTile extends SonarInventory {

	public final ISonarInventoryTile tile;

	public SonarInventoryTile(ISonarInventoryTile tile) {
		this(tile, 1);
	}

	public SonarInventoryTile(ISonarInventoryTile tile, int size) {
		super(size);
		this.tile = tile;
		this.getInsertFilters().put((slot, stack, face) -> tile.checkInsert(slot, stack, face, EnumFilterType.INTERNAL), EnumFilterType.INTERNAL);
		this.getInsertFilters().put((slot, stack, face) -> tile.checkInsert(slot, stack, face, EnumFilterType.EXTERNAL), EnumFilterType.EXTERNAL);
		this.getExtractFilters().put((slot, count, face) -> tile.checkExtract(slot, count, face, EnumFilterType.INTERNAL), EnumFilterType.INTERNAL);
		this.getExtractFilters().put((slot, count, face) -> tile.checkExtract(slot, count, face, EnumFilterType.EXTERNAL), EnumFilterType.EXTERNAL);
	}

	@Override
	public IInventory getWrapperInventory() {
		if (!(tile instanceof TileEntity)) {
			return super.getWrapperInventory();
		}
		return wrapped_inv == null ? wrapped_inv = new IInventoryWrapper(this, (TileEntity) tile) : wrapped_inv;
	}

	@Override
	public boolean checkDrop(int slot, @Nonnull ItemStack stack) {
		return tile.checkDrop(slot, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		tile.onInventoryContentsChanged(slot);
	}
}
