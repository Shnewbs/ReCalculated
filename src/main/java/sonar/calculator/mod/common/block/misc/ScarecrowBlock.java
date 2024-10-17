package sonar.calculator.mod.common.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.core.common.block.SonarBlock;

import javax.annotation.Nonnull;
import java.util.Random;

public class ScarecrowBlock extends SonarBlock {

	public ScarecrowBlock() {
		super(Material.CLOTH, false);
		this.hasSpecialRenderer = true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.setBlockToAir(pos);
		for (int i = 1; i < 3; i++) {
			BlockPos offset = pos.offset(EnumFacing.DOWN, i);
			IBlockState offsetState = world.getBlockState(offset);
			Block block = world.getBlockState(offset).getBlock();
			if (block == Calculator.scarecrow) {
				block.dropBlockAsItem(world, offset, offsetState, 0);
				world.setBlockToAir(offset);
			}
		}
	}

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player){
    	return new ItemStack(Calculator.scarecrow, 1);
    }
    
	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

    @Nonnull
    @Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

    @Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

    @Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

    @Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this);
	}
}
