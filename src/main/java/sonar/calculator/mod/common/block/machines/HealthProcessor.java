package sonar.calculator.mod.common.block.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.machines.TileEntityHealthProcessor;
import sonar.core.common.block.SonarMaterials;
import sonar.core.common.block.SonarSidedBlock;
import sonar.core.helpers.FontHelper;
import sonar.core.network.FlexibleGuiHandler;
import sonar.core.utils.ISpecialTooltip;

import javax.annotation.Nonnull;
import java.util.List;

public class HealthProcessor extends SonarSidedBlock implements ISpecialTooltip {

	public HealthProcessor() {
		super(SonarMaterials.machine, true);
	}

	@Override
	public boolean hasAnimatedFront() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        player.getHeldItemMainhand();
        if (player.getHeldItemMainhand().getItem() == Calculator.wrench) {
			return false;
		}
		if (!world.isRemote) {
			FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int i) {
		return new TileEntityHealthProcessor();
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
		int health = tag == null ? 0 : tag.getInteger("Food");
		if (health != 0) {
			list.add(FontHelper.translate("points.health") + ": " + health);
		}
	}
}
