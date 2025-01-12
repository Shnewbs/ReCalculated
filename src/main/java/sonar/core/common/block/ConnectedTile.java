package sonar.core.common.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sonar.core.api.blocks.IConnectedBlock;
import sonar.core.utils.ISpecialTooltip;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public abstract class ConnectedTile extends SonarBlock implements IConnectedBlock, ITileEntityProvider, ISpecialTooltip {

	protected ConnectedTile(int target) {
		super(SonarMaterials.machine, false);
		this.target = target;
	}

	public int target;
	public static final PropertySonarFacing NORTH = PropertySonarFacing.create("north", EnumFacing.NORTH);
	public static final PropertySonarFacing EAST = PropertySonarFacing.create("east", EnumFacing.EAST);
	public static final PropertySonarFacing SOUTH = PropertySonarFacing.create("south", EnumFacing.SOUTH);
	public static final PropertySonarFacing WEST = PropertySonarFacing.create("west", EnumFacing.WEST);
	public static final PropertySonarFacing DOWN = PropertySonarFacing.create("down", EnumFacing.DOWN);
	public static final PropertySonarFacing UP = PropertySonarFacing.create("up", EnumFacing.UP);
	public static final ArrayList<PropertySonarFacing> faces = Lists.newArrayList(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	public static final ArrayList<PropertySonarFacing> horizontals = Lists.newArrayList(NORTH, SOUTH, WEST, EAST);

	public static class PropertySonarFacing extends PropertyBool {
		public EnumFacing facing;

		protected PropertySonarFacing(String name, EnumFacing facing) {
			super(name);
			this.facing = facing;
		}

		public static PropertySonarFacing create(String name, EnumFacing facing) {
			return new PropertySonarFacing(name, facing);
		}
	}

	public boolean checkBlockInDirection(IBlockAccess world, int x, int y, int z, EnumFacing side) {
		IBlockState state = world.getBlockState(new BlockPos(x, y, z));
		IBlockState block = world.getBlockState(new BlockPos(x + side.getFrontOffsetX(), y + side.getFrontOffsetY(), z + side.getFrontOffsetZ()));
		int meta = state.getBlock().getMetaFromState(state);
		return type(state, block, meta, block.getBlock().getMetaFromState(block));
	}

	public static boolean type(IBlockState state1, IBlockState state2, int m1, int m2) {
		Block block1 = state1.getBlock();
		Block block2 = state2.getBlock();
		if (!(block1 instanceof IConnectedBlock) || !(block2 instanceof IConnectedBlock) || m1 == m2) {
			if (block1 instanceof IConnectedBlock) {
				IConnectedBlock c1 = (IConnectedBlock) block1;
				int[] connections1 = ((IConnectedBlock) block1).getConnections();

				if (block2 instanceof IConnectedBlock) {
					int[] connections2 = ((IConnectedBlock) block2).getConnections();
					for (int aConnections1 : connections1) {
						for (int aConnections2 : connections2) {
							if (aConnections1 == aConnections2)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Nonnull
	@Override
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess w, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		return state.withProperty(NORTH, checkBlockInDirection(w, x, y, z, EnumFacing.NORTH)).withProperty(SOUTH, checkBlockInDirection(w, x, y, z, EnumFacing.SOUTH)).withProperty(WEST, checkBlockInDirection(w, x, y, z, EnumFacing.WEST)).withProperty(EAST, checkBlockInDirection(w, x, y, z, EnumFacing.EAST)).withProperty(UP, checkBlockInDirection(w, x, y, z, EnumFacing.UP)).withProperty(DOWN, checkBlockInDirection(w, x, y, z, EnumFacing.DOWN));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, DOWN, UP);
	}

	@Override
	public int[] getConnections() {
		return new int[] { target };
	}
}
