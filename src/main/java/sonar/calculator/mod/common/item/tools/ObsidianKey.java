package sonar.calculator.mod.common.item.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.api.blocks.IObsidianDrop;
import sonar.core.common.item.SonarItem;

import javax.annotation.Nonnull;

public class ObsidianKey extends SonarItem {

	public ObsidianKey() {
		setMaxDamage(1000);
		this.maxStackSize = 1;
	}

	@Nonnull
    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!player.canPlayerEdit(pos, side, stack)) {
			return EnumActionResult.PASS;
		}
		Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.OBSIDIAN || block instanceof IObsidianDrop && ((IObsidianDrop) block).canKeyDrop(world, pos)) {
			block.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
			world.setBlockToAir(pos);
			stack.damageItem(1, player);
		}

		return EnumActionResult.SUCCESS;
	}
}
