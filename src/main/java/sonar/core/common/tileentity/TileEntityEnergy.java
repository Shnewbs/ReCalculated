package sonar.core.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.utils.ActionType;
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
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.darkhax.tesla.capability.TeslaCapabilities;

import javax.annotation.Nonnull;
import java.util.List;

@Optional.InterfaceList({
		@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux"),
		@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux"),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "ic2"),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2")
})
public abstract class TileEntityEnergy extends BlockEntity implements IEnergyReceiver, IEnergyProvider, ISonarEnergyTile, IEnergyTile, IEnergySink, IEnergySource {

	public EnergyMode energyMode = EnergyMode.RECIEVE;
	public final SyncSidedEnergyStorage storage = new SyncSidedEnergyStorage(this, 0);
	boolean IC2Connected = false;

	public TileEntityEnergy(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		syncList.addPart(storage);
	}

	public void setEnergyMode(EnergyMode mode) {
		energyMode = mode;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		readData(tag, NBTHelper.SyncType.SAVE);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeData(tag, NBTHelper.SyncType.SAVE);
	}

	public void addEnergy(Direction... faces) {
		EnergyTransferHandler.INSTANCE_SC.transferToAdjacent(this, SonarHelper.getEnumFacingValues(), storage.getMaxExtract());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		EnergyMode mode = getModeForSide(facing);
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		if (SonarLoader.teslaLoaded && mode.canConnect()) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve() || capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend() || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		EnergyMode mode = getModeForSide(facing);
		if (CapabilityEnergy.ENERGY == capability) {
			return (T) storage.getOrCreateWrapper(facing);
		}
		if (mode != null && SonarLoader.teslaLoaded && mode.canConnect()) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve() || capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend() || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) storage.getOrCreateWrapper(facing);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public EnergyMode getModeForSide(Direction side) {
		if (side == null) {
			return EnergyMode.SEND_RECIEVE;
		}
		return energyMode;
	}

	@Override
	public SyncEnergyStorage getStorage() {
		return storage;
	}

	@Override
	public EnergyMode getMode() {
		return energyMode;
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public boolean canConnectEnergy(Direction from) {
		return getModeForSide(from).canConnect();
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int getEnergyStored(Direction from) {
		return storage.getOrCreateWrapper(from).getEnergyStored();
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int getMaxEnergyStored(Direction from) {
		return storage.getOrCreateWrapper(from).getMaxEnergyStored();
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int extractEnergy(Direction from, int maxExtract, boolean simulate) {
		if (energyMode.canSend())
			return storage.getOrCreateWrapper(from).extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int receiveEnergy(Direction from, int maxReceive, boolean simulate) {
		if (energyMode.canRecieve())
			return storage.getOrCreateWrapper(from).receiveEnergy(maxReceive, simulate);
		return 0;
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void onLoad() {
		super.onLoad();
		if (!this.level.isClientSide && !IC2Connected) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			IC2Connected = true;
		}
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void setRemoved() {
		super.setRemoved();
		if (!this.level.isClientSide && IC2Connected) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			IC2Connected = false;
		}
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if (!this.level.isClientSide && IC2Connected) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			IC2Connected = false;
		}
	}

	@Override
	@Optional.Method(modid = "ic2")
	public double getDemandedEnergy() {
		return Math.min(EUHelper.getVoltage(this.getSinkTier()), this.storage.addEnergy((long)(this.storage.getMaxReceive() / 4), null, ActionType.getTypeForAction(true)));
	}

	@Override
	@Optional.Method(modid = "ic2")
	public int getSinkTier() {
		return 4;
	}

	@Override
	@Optional.Method(modid = "ic2")
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
		return this.getModeForSide(side).canRecieve();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public double injectEnergy(Direction directionFrom, double amount, double voltage) {
		int addRF = this.storage.getOrCreateWrapper(directionFrom).receiveEnergy((int) amount * 4, true);
		this.storage.getOrCreateWrapper(directionFrom).addEnergy(addRF, ActionType.getTypeForAction(false));
		return amount - addRF / 4;
	}

	@Override
	@Optional.Method(modid = "ic2")
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, Direction side) {
		return getModeForSide(side).canSend();
	}

	@Override
	@Optional.Method(modid = "ic2")
	public double getOfferedEnergy() {
		return Math.min(EUHelper.getVoltage(this.getSourceTier()), this.storage.removeEnergy(storage.getMaxExtract(), null, ActionType.getTypeForAction(true)) / 4);
	}

	@Override
	@Optional.Method(modid = "ic2")
	public void drawEnergy(double amount) {
		this.storage.removeEnergy((long) (amount * 4), null, ActionType.getTypeForAction(false));
	}

	@Override
	@Optional.Method(modid = "ic2")
	public int getSourceTier() {
		return 4;
	}
}
