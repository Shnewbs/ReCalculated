package sonar.calculator.mod.common.block.generators;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.calculator.mod.api.items.IStability;
import sonar.calculator.mod.common.tileentity.generators.TileEntityCalculatorPlug;
import sonar.core.common.block.SonarBlockContainer;
import sonar.core.common.block.SonarMaterials;
import sonar.core.handlers.inventories.handling.ItemTransferHelper;
import sonar.core.network.FlexibleGuiHandler;

import javax.annotation.Nonnull;

public class CalculatorPlug extends SonarBlockContainer {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public CalculatorPlug() {
		super(SonarMaterials.machine, false);
		this.hasSpecialRenderer = true;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, Boolean.TRUE));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ItemStack held = player.getHeldItem(hand);
			if (!held.isEmpty() && held.getItem() instanceof IStability) {
				IItemHandler handler = ItemTransferHelper.getItemHandler(world, pos, EnumFacing.UP);
				player.setHeldItem(hand, ItemHandlerHelper.insertItem(handler, held, false));
				return true;
			}
			FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
		return new TileEntityCalculatorPlug();
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
