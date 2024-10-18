package sonar.core.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.core.helpers.FontHelper;
import sonar.core.integration.SonarLoader;

import javax.annotation.Nonnull;
import java.util.List;

public class SonarSeeds extends Item implements IPlantable {
	private Block cropBlock;
	private Block soilBlock;
	public int greenhouseTier;

	public SonarSeeds(Block cropBlock, Block soilBlock, int tier, Item.Properties properties) {
		super(properties);
		this.cropBlock = cropBlock;
		this.soilBlock = soilBlock;
		this.greenhouseTier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<String> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		if (SonarLoader.calculatorLoaded()) {
			switch (greenhouseTier) {
				case 1:
					tooltip.add(FontHelper.translate("planting.basic"));
					break;
				case 2:
					tooltip.add(FontHelper.translate("planting.advanced"));
					break;
				case 3:
					tooltip.add(FontHelper.translate("planting.flawless"));
					break;
			}
		}
	}

	@Nonnull
	@Override
	public InteractionResult useOn(Player player, Level world, BlockPos pos, InteractionHand hand, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getItemInHand(hand);
		if (this.greenhouseTier == 0 || !SonarLoader.calculatorLoaded()) {
			if (hand == InteractionHand.MAIN_HAND && player.mayUseItemAt(pos, player.getDirection(), stack) && player.mayUseItemAt(pos.above(), player.getDirection(), stack)) {
				BlockState state = world.getBlockState(pos);
				if (state.canSustainPlant(world, pos, player.getDirection(), this) && world.isEmptyBlock(pos.above())) {
					world.setBlock(pos.above(), cropBlock.defaultBlockState(), 3);
					stack.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return cropBlock == net.minecraft.world.level.block.Blocks.NETHER_WART ? PlantType.NETHER : PlantType.CROP;
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		return cropBlock.defaultBlockState();
	}

	public boolean canTierUse(int tier) {
		return tier >= this.greenhouseTier;
	}
}
