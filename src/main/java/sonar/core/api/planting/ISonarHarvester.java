package sonar.core.api.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for handling automated harvesting of crops or blocks.
 */
public interface ISonarHarvester {

    /**
     * Checks if the block at the given position can be harvested.
     *
     * @param world The world object.
     * @param pos The block position.
     * @param state The current block state.
     * @return True if the block can be harvested, false otherwise.
     */
    boolean canHarvest(World world, BlockPos pos, IBlockState state);

    /**
     * Checks if the block is ready for harvesting.
     *
     * @param world The world object.
     * @param pos The block position.
     * @param state The current block state.
     * @return True if the block is ready to be harvested, false otherwise.
     */
    boolean isReady(World world, BlockPos pos, IBlockState state);

    /**
     * Performs the harvest on the block.
     *
     * @param drops The list of item drops.
     * @param world The world object.
     * @param pos The block position.
     * @param state The current block state.
     * @param fortune The fortune level applied to the harvest.
     * @param keepBlock Whether to keep the block after harvesting.
     * @return True if the harvest was successful, false otherwise.
     */
    boolean doHarvest(NonNullList<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, boolean keepBlock);
}
