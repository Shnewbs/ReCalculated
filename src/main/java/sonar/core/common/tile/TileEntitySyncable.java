package sonar.core.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.network.PacketTileSync;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.IValueWatcher;
import sonar.core.sync.ValueWatcher;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntitySyncable extends TileEntitySaveable {

    public ListenableList<PlayerListener> listeners = new ListenableList<>(new Listener(this), 1);

    public TileEntitySyncable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean hasSyncListeners() {
        return listeners.hasListeners(0);
    }

    public List<PlayerListener> getSyncListeners() {
        return listeners.getListeners(0);
    }

    @Override
    public void onValuesChanged() {
        super.onValuesChanged();
        if (!level.isClientSide && hasSyncListeners()) {
            performSync();
        }
    }

    public void performSync() {
        if (!level.isClientSide) {
            CompoundTag tag = writeData(new CompoundTag(), NBTHelper.SyncType.DEFAULT_SYNC);
            if (!tag.isEmpty()) {
                getSyncListeners().forEach(l -> SonarCore.network.sendTo(new PacketTileSync(this.getBlockPos(), tag, NBTHelper.SyncType.DEFAULT_SYNC), l.player));
            }
        }
    }

    public void sendFirstPacket(ServerPlayer player) {
        if (!level.isClientSide) {
            CompoundTag tag = writeData(new CompoundTag(), NBTHelper.SyncType.SAVE);
            SonarCore.network.sendTo(new PacketTileSync(this.getBlockPos(), tag, NBTHelper.SyncType.SAVE), player);
        }
    }

    public void sendLastPacket(ServerPlayer player) {}

    @Override
    public void setRemoved() {
        super.setRemoved();
        listeners.invalidateList();
    }

    public static class Listener implements ISonarListenable<PlayerListener> {

        public final TileEntitySyncable tile;

        public Listener(TileEntitySyncable tile) {
            this.tile = tile;
        }

        public void onListenerAdded(ListenerTally<PlayerListener> tally) {
            tile.sendFirstPacket(tally.listener.player);
        }

        public void onListenerRemoved(ListenerTally<PlayerListener> tally) {
            tile.sendLastPacket(tally.listener.player);
        }

        @Override
        public ListenableList<PlayerListener> getListenerList() {
            return tile.listeners;
        }

        @Override
        public boolean isValid() {
            return !tile.isRemoved();
        }
    }
}
