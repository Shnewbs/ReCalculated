package sonar.core.api.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface implemented on blocks that can be interacted with a wrench.
 * Provides default behavior for wrench interactions.
 */
public interface IWrenchable {

    /**
     * Called when the block is wrenched by a player.
     */
    default void onWrenched(EntityPlayer player, World world, BlockPos pos) {
        // Default empty implementation, can be overridden.
    }

    /**
     * Determines if the block can be wrenched by a player.
     *
     * @return true if the block can be wrenched, false otherwise.
     */
    default boolean canWrench(EntityPlayer player, World world, BlockPos pos) {
        return true; // Default allows wrenching, can be overridden.
    }
}
