package sonar.core.api.planting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for handling planting actions and conditions in automated systems.
 */
public interface ISonarPlanter {

    /**
     * Determines if a seed can be planted at a specific tier.
     *
     * @param seed The seed to check.
     * @param tier The tier level.
     * @return True if the seed can be planted at the given tier, false by default.
     */
    default boolean canTierPlant(ItemStack seed, int tier) { return true; }

    /**
     * Checks if the item is a plantable seed.
     *
     * @param seed The item stack to check.
     * @return True if the seed is plantable.
     */
    boolean isPlantable(ItemStack seed);

    /**
     * Checks if a seed can be planted at the given position.
     *
     * @param seed The seed item stack.
     * @param world The world object.
     * @param pos The position where the seed would be planted.
     * @return True if the seed can be planted.
     */
    boolean canPlant(ItemStack seed, World world, BlockPos pos);

    /**
     * Performs the planting of the seed at the given position.
     *
     * @param seed The seed item stack.
     * @param world The world object.
     * @param pos The position to plant the seed.
     * @return True if the planting was successful.
     */
    boolean doPlant(ItemStack seed, World world, BlockPos pos);
}
