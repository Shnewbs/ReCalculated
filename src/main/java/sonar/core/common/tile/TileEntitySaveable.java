package sonar.core.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.IValueWatcher;
import sonar.core.sync.ValueWatcher;

import javax.annotation.Nonnull;

public class TileEntitySaveable extends BlockEntity implements TickableBlockEntity, INBTSyncable {

    public final ValueWatcher value_watcher = new ValueWatcher(new IValueWatcher() {
        @Override
        public void onSyncValueChanged(ISonarValue value) {
            onInternalValueChanged(value);
        }
    });

    public TileEntitySaveable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onValuesChanged() {}

    /** Level may be null */
    public void onInternalValueChanged(ISonarValue value) {}

    @Override
    public void tick() {
        if (value_watcher.isDirty()) {
            onValuesChanged();
            value_watcher.forEachSyncable(s -> s.setDirty(false));
            value_watcher.setDirty(false);
            if (!level.isClientSide)
                setChanged();
        }
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

    @Override
    public void readData(CompoundTag nbt, NBTHelper.SyncType type) {
        value_watcher.forEachSyncable(v -> {
            if (v.canLoadFrom(nbt)) v.load(nbt);
        });
    }

    @Override
    public CompoundTag writeData(CompoundTag nbt, NBTHelper.SyncType type) {
        value_watcher.forEachSyncable(v -> v.save(nbt));
        return nbt;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    public NBTHelper.SyncType getUpdateTagType() {
        return NBTHelper.SyncType.SYNC_OVERRIDE;
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
    public boolean shouldRefresh(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
