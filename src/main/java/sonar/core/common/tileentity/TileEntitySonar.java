package sonar.core.common.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.SonarCore;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncableList;
import sonar.core.utils.IWorldPosition;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntitySonar extends BlockEntity implements ISyncableListener, BlockEntityTicker<TileEntitySonar>, INBTSyncable, IWailaInfo, IWorldPosition {

	public SyncableList syncList = new SyncableList(this);
	protected boolean forceSync;
	protected BlockCoords coords = BlockCoords.EMPTY;
	public boolean loaded = true;
	public boolean isDirty;

	public TileEntitySonar(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public boolean isClient() {
		return level == null ? false : level.isClientSide;
	}

	public boolean isServer() {
		return level == null ? true : !level.isClientSide;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, Direction facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability && this instanceof ISonarInventoryTile) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, Direction facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability && this instanceof ISonarInventoryTile) {
			return (T) ((ISonarInventoryTile) this).inv().getItemHandler(facing);
		}
		return super.getCapability(capability, facing);
	}

	public void onFirstTick() {
		// this.markBlockForUpdate();
		// setChanged();
	}

	@Override
	public BlockCoords getCoords() {
		if (coords == BlockCoords.EMPTY) {
			coords = new BlockCoords(this);
		}
		return coords;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		readData(nbt, SyncType.SAVE);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeData(tag, SyncType.SAVE);
	}

	/** things like inventories are generally only sent with SyncType.SAVE */
	public SyncType getUpdateTagType() {
		return SyncType.SYNC_OVERRIDE;
	}

	@Nonnull
	@Override
	public CompoundTag getUpdateTag() {
		return writeData(super.getUpdateTag(), getUpdateTagType());
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		readData(tag, getUpdateTagType());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = saveWithFullMetadata();
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		load(pkt.getTag());
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		coords = new BlockCoords(this);
	}

	@Override
	public void readData(CompoundTag nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncList);
	}

	@Override
	public CompoundTag writeData(CompoundTag nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		NBTHelper.writeSyncParts(nbt, type, syncList, forceSync);
		return nbt;
	}

	@Override
	public List<String> getWailaInfo(List<String> currenttip, BlockState state) {
		return currenttip;
	}

	public void forceNextSync() {
		forceSync = true;
		setChanged();
	}

	public void onSyncPacketRequested(Player player) {}

	public void requestSyncPacket() {
		SonarCore.network.sendToServer(new PacketRequestSync(worldPosition));
	}

	public void sendSyncPacket(Player player) {
		sendSyncPacket(player, SyncType.SYNC_OVERRIDE);
	}

	public void sendSyncPacket(Player player, SyncType type) {
		if (level.isClientSide) {
			return;
		}
		if (player instanceof ServerPlayer) {
			CompoundTag tag = new CompoundTag();
			writeData(tag, type);
			if (!tag.isEmpty()) {
				SonarCore.network.sendTo(createSyncPacket(tag, type), (ServerPlayer) player);
			}
		}
	}

	public IMessage createRequestPacket() {
		return new PacketRequestSync(worldPosition);
	}

	public IMessage createSyncPacket(CompoundTag tag, SyncType type) {
		return new PacketTileSync(worldPosition, tag, type);
	}

	public void markBlockForUpdate() {
		if (isServer()) {
			setChanged();
			SonarCore.sendFullSyncAroundWithRenderUpdate(this, 128);
		} else {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public boolean maxRender() {
		return false;
	}

	public void openFlexibleGui(Player player, int id) {
		SonarCore.instance.guiHandler.openBasicTile(false, player, level, worldPosition, id);
	}

	public void changeFlexibleGui(Player player, int id) {
		SonarCore.instance.guiHandler.openBasicTile(true, player, level, worldPosition, id);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return maxRender() ? 65536.0D : super.getMaxRenderDistanceSquared();
	}

	@Nonnull
	@Override
	public AABB getRenderBoundingBox() {
		return maxRender() ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

	@Override
	public void markChanged(IDirtyPart part) {
		if (this.isServer()) {
			syncList.markSyncPartChanged(part);
			isDirty = true;
		}
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state, TileEntitySonar blockEntity) {
		if (loaded) {
			onFirstTick();
			loaded = false;
		}
		if (isDirty) {
			this.setChanged();
			isDirty = false;
		}
	}

	@Override
	public boolean shouldRefresh(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
