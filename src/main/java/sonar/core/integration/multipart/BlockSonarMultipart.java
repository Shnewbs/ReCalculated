package sonar.core.integration.multipart;

import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Abstract class representing a block that can contain multiple parts.
 */
public abstract class BlockSonarMultipart extends BlockContainer implements IMultipart {

	protected BlockSonarMultipart(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean canPlacePartAt(World world, BlockPos pos) {
		return true; // You might want to implement actual logic here based on your requirements.
	}

	@Override
	public boolean canPlacePartOnSide(World world, BlockPos pos, EnumFacing side, IPartSlot slot) {
		return true; // You might want to implement actual logic here based on your requirements.
	}
}
