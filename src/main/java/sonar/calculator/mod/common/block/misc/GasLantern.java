package sonar.calculator.mod.common.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.misc.TileEntityGasLantern;
import sonar.core.common.block.SonarBlockContainer;
import sonar.core.common.block.SonarMaterials;
import sonar.core.network.FlexibleGuiHandler;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Random;

public class GasLantern extends SonarBlockContainer {

	private static boolean keepInventory;
	public final boolean isActive;
	public static final PropertyDirection DIR = PropertyDirection.create("dir");

	public GasLantern(boolean active) {
		super(SonarMaterials.machine, false);
		this.isActive = active;
		this.hasSpecialRenderer = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(DIR, EnumFacing.NORTH));
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && player != null) {
			FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		if (state.getBlock() == Calculator.gas_lantern_on) {
			float x1 = pos.getX() + random.nextFloat();
			float y1 = pos.getY() + 0.5F;
			float z1 = pos.getZ() + random.nextFloat();
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
		}
	}

    @Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			super.breakBlock(world, pos, state);
		}
		world.removeTileEntity(pos);
	}

	/* @Override public void onBlockAdded(World world, BlockPos pos, IBlockState state) { super.onBlockAdded(world, pos, state); setDefaultFacing(world, pos, state); }
	 *
	 * public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbour) { super.onNeighborChange(world, pos, neighbour); try { //setDefaultFacing((World) world, pos, world.getBlockState(pos)); } catch (ClassCastException exception) { Calculator.logger.error("Lantern: Tried to cast IBlockAccess to World"); } } */
	private EnumFacing getDefaultFacing(IBlockAccess world, BlockPos pos, IBlockState state) {
		if (world != null) {
			Iterator<EnumFacing> iterator = EnumFacing.Plane.VERTICAL.iterator();
			boolean vertical = false;
			do {
				if (!iterator.hasNext()) {
                    vertical = true;
                    iterator = EnumFacing.Plane.HORIZONTAL.iterator();
                }
				EnumFacing facing = iterator.next();
				IBlockState stateOff = world.getBlockState(pos.offset(facing));
				Block block = stateOff.getBlock();
				if (block.isFullBlock(stateOff)) {
					return facing;
				}
			} while (iterator.hasNext() || !vertical);
		}
		return EnumFacing.DOWN;
	}

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		keepInventory = true;
		if (active) {
			worldIn.setBlockState(pos, Calculator.gas_lantern_on.getDefaultState().withProperty(DIR, iblockstate.getValue(DIR)), 3);
		} else {
			worldIn.setBlockState(pos, Calculator.gas_lantern_off.getDefaultState().withProperty(DIR, iblockstate.getValue(DIR)), 3);
		}
		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}

    @Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing dir = getDefaultFacing(world, pos, state);
        return new AxisAlignedBB(0.3F + dir.getFrontOffsetX() * 0.32F, 0.0F + getY(dir), 0.3F + dir.getFrontOffsetZ() * 0.32F, 0.7F + dir.getFrontOffsetX() * 0.32F, 0.7F + getY(dir), 0.7F + dir.getFrontOffsetZ() * 0.32F);
	}

	public float getY(EnumFacing meta) {
		if (meta == EnumFacing.DOWN) {
			return 0.0F;
		} else {
			return 0.1F;
		}
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileEntityGasLantern();
	}

    @Nonnull
    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Calculator.gas_lantern_off);
	}

    @Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty(DIR, enumfacing);
	}

    @Override
	public int getMetaFromState(IBlockState state) {
        return state.getValue(DIR).getIndex();
	}

    @Nonnull
    @Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(DIR, getDefaultFacing(world, pos, state));
	}

    @Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DIR);
	}
}
