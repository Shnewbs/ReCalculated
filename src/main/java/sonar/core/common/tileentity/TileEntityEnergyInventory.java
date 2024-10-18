package sonar.core.common.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.energy.DischargeValues;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.inventories.SonarInventoryTile;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.EUHelper;
import sonar.core.integration.SonarLoader;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.network.sync.SyncSidedEnergyStorage;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.IValueWatcher;
import sonar.core.sync.ValueWatcher;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntityEnergyInventory extends TileEntityEnergy implements ISonarInventoryTile {

	public int CHARGING_RATE;

	public final SonarInventoryTile inv = new SonarInventoryTile(this);
	{
		syncList.addPart(inv);
	}

	public TileEntityEnergyInventory(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ISonarInventory inv() {
		return inv;
	}

	public List<ItemStack> slots() {
		return inv.slots();
	}

	public void charge(int id) {
		long maxTransfer = CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxReceive()) : getStorage().getMaxReceive();
		EnergyTransferHandler.INSTANCE_SC.chargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
	}

	public void discharge(int id) {
		long maxTransfer = CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxExtract()) : getStorage().getMaxExtract();
		long transferred = EnergyTransferHandler.INSTANCE_SC.dischargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
		if (transferred == 0) { // if no energy is stored, check if the item is a dischargeable item (e.g. redstone, coal)
			int value = DischargeValues.instance().getValue(slots().get(id));
			if (value > 0 && storage.getEnergyLevel() + value <= storage.getFullCapacity()) {
				storage.setEnergyStored(value + storage.getEnergyLevel());
				slots().get(id).shrink(1);
			}
		}
	}
}
