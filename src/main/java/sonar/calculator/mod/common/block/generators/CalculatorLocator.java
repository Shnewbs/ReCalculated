package sonar.calculator.mod.common.block.generators;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.generators.TileEntityCalculatorLocator;
import sonar.calculator.mod.utils.helpers.CalculatorHelper;
import sonar.core.SonarCore;
import sonar.core.api.blocks.IStableBlock;
import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.network.FlexibleGuiHandler;
import sonar.core.utils.ISpecialTooltip;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class CalculatorLocator extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public CalculatorLocator() {
		super(SonarMaterials.machine, false);
		this.hasSpecialRenderer = true;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, true));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player != null && !world.isRemote) {
            TileEntity locator = world.getTileEntity(pos);
			if (locator != null) {
				SonarCore.sendFullSyncAround(locator, 64);
			}
			FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		TileEntity target = world.getTileEntity(pos);
		if (target instanceof TileEntityCalculatorLocator) {
			TileEntityCalculatorLocator locator = (TileEntityCalculatorLocator) target;
			if (locator.active.getObject()) {
				float x1 = pos.getX() + random.nextFloat();
				float y1 = pos.getY() + 0.5F;
				float z1 = pos.getZ() + random.nextFloat();

				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x1, y1, z1, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public static int multiBlockStructure(World world, BlockPos pos) {
		for (int size = 1; size < 12; size++) {
			if (checkSize(world, pos, size)) {
				return size;
			}
		}
		return 0;
	}

	public static boolean checkSize(World world, BlockPos pos, int size) {
		for (int X = -size; X <= size; X++) {
			for (int Z = -size; Z <= size; Z++) {
				if (!(X == 0 && Z == 0)) {
					if (!(world.getBlockState(pos.add(X, -1, Z)).getBlock() instanceof IStableBlock)) {
						return false;
					}
				}
			}
		}

        for (int XZ = -size; XZ <= size; XZ++) {
			for (int Y = -1; Y <= 0; Y++) {
				if (!(world.getBlockState(pos.add(XZ, Y, size + 1)).getBlock() instanceof IStableBlock)) {
					return false;
				} else if (!(world.getBlockState(pos.add(XZ, Y, -(size + 1))).getBlock() instanceof IStableBlock)) {
					return false;
                } else if (!(world.getBlockState(pos.add(size + 1, Y, XZ)).getBlock() instanceof IStableBlock)) {
					return false;
				} else if (!(world.getBlockState(pos.add(-(size + 1), Y, XZ)).getBlock() instanceof IStableBlock)) {
					return false;
				}
			}
		}

        for (int X = -size; X <= size; X++) {
            for (int Z = -size; Z <= size; Z++) {
				if (!(X == 0 && Z == 0)) {
					if (!(world.getBlockState(pos.add(X, 0, Z)).getBlock() == Calculator.calculatorplug)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileEntityCalculatorLocator();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        CalculatorHelper.addEnergytoToolTip(stack, world, list);
    }

    @Override
	public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, meta == 1);
	}

    @Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE) ? 1 : 0;
	}

    @Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
	}
}
