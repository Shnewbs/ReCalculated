package sonar.core.handlers.fluids.tiles;

import java.util.List;

import appeng.api.networking.security.IActionHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.FluidHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;

@FluidHandler(modid = "appliedenergistics2", priority = 4)
public class AE2FluidHandler implements ISonarFluidHandler {

	@Override
	public boolean canHandleFluids(BlockEntity tile, Direction dir) {
		return tile instanceof IGridProxyable;
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> storedStacks, BlockEntity tile, Direction dir) {
		long maxStorage = 0;
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IItemList<IAEFluidStack> fluids = AE2Helper.getFluidChannel(proxy.getProxy().getStorage()).getStorageList();
			if (fluids == null) {
				return StorageSize.EMPTY;
			}
			for (IAEFluidStack fluid : fluids) {
				SonarAPI.getFluidHelper().addFluidToList(storedStacks, AE2Helper.convertAEFluidStack(fluid));
				maxStorage += fluid.getStackSize();
			}
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return new StorageSize(maxStorage, maxStorage);
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, BlockEntity tile, Direction dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IAEFluidStack fluid = AE2Helper.getFluidChannel(proxy.getProxy().getStorage()).injectItems(AE2Helper.convertStoredFluidStack(add), AE2Helper.getActionable(action), new MachineSource((IActionHost) tile));
			if (fluid == null || fluid.getStackSize() == 0) {
				return null;
			}
			return AE2Helper.convertAEFluidStack(fluid);
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, BlockEntity tile, Direction dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			StoredFluidStack fluid = SonarAPI.getFluidHelper().getStackToAdd(remove.stored, remove, AE2Helper.convertAEFluidStack(AE2Helper.getFluidChannel(proxy.getProxy().getStorage()).extractItems(AE2Helper.convertStoredFluidStack(remove), AE2Helper.getActionable(action), new MachineSource((IActionHost) tile))));
			if (fluid == null || fluid.stored == 0) {
				return null;
			}
			return fluid;
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return remove;
	}
}
