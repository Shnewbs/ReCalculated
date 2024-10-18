package sonar.core.api.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for objects that can act as fertilizers, supporting growth and checking fertilization conditions.
 */
public interface ISonarFertiliser {

	/**
	 * Checks if the block at the given position can be fertilized.
	 *
	 * @param world The world object.
	 * @param pos The block position.
	 * @param state The current block state.
	 * @return True if the block can be fertilized, false otherwise.
	 */
	boolean canFertilise(World world, BlockPos pos, IBlockState state);

	/**
	 * Checks if the block at the given position can grow.
	 *
	 * @param world The world object.
	 * @param pos The block position.
	 * @param state The current block state.
	 * @return True if the block can grow, false otherwise.
	 */
	boolean canGrow(World world, BlockPos pos, IBlockState state);

	/**
	 * Causes the block at the given position to grow.
	 *
	 * @param world The world object.
	 * @param pos The block position.
	 * @param state The current block state.
	 * @return True if growth was successful, false otherwise.
	 */
	boolean grow(World world, BlockPos pos, IBlockState state);
}
