package sonar.core.common.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerListener;

public class ContainerSyncable extends Container {

    public TileEntitySyncable tile;

    public ContainerSyncable(TileEntitySyncable tile) {
        this.tile = tile;
    }

    public void addListener(ContainerListener listener) {
        super.addListener(listener);
        if (!tile.getLevel().isClientSide && listener instanceof Player) {
            tile.listeners.addListener((Player) listener, 0);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!tile.getLevel().isClientSide) {
            tile.listeners.removeListener(player, true, 0);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(tile.getBlockPos().getX() + 0.5D, tile.getBlockPos().getY() + 0.5D, tile.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }
}
