package sonar.calculator.mod.common.block.machines;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.common.tileentity.TileEntityGreenhouse.State;
import sonar.calculator.mod.common.tileentity.machines.TileEntityFlawlessGreenhouse;
import sonar.calculator.mod.utils.helpers.CalculatorHelper;
import sonar.core.api.blocks.IConnectedBlock;
import sonar.core.common.block.SonarBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.FontHelper;
import sonar.core.network.FlexibleGuiHandler;
import sonar.core.utils.FailedCoords;
import sonar.core.utils.ISpecialTooltip;

import javax.annotation.Nonnull;
import java.util.List;

public class FlawlessGreenhouse extends SonarBlock implements IConnectedBlock, ITileEntityProvider, ISpecialTooltip {

	public int[] connections = new int[]{0,5,6};
	
	public FlawlessGreenhouse() {
		super(SonarMaterials.machine, true);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityFlawlessGreenhouse) {
			TileEntityFlawlessGreenhouse house = (TileEntityFlawlessGreenhouse) tile;
			if (player.isSneaking()) {
				if (house.houseState.getObject() == State.INCOMPLETE) {
					FailedCoords coords = house.checkStructure(null);
					if (!coords.getBoolean()) {
                        FontHelper.sendMessage("X: " + coords.getCoords().getX() + " Y: " + coords.getCoords().getY() + " Z: " + coords.getCoords().getZ() + " - " + FontHelper.translate("greenhouse.equal") + ' ' + coords.getBlock(), world, player);
					}
				} else if (house.houseState.getObject() == State.COMPLETED) {
					FontHelper.sendMessage(FontHelper.translate("greenhouse.complete"), world, player);
				}
			} else {
				if (player != null) {
					if (!world.isRemote) {
						FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
					}
				}
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileEntityFlawlessGreenhouse();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        CalculatorHelper.addEnergytoToolTip(stack, world, list);
	}

	@Override
	public int[] getConnections() {
		return connections;
	}
}
