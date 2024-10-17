package sonar.calculator.mod.common.item.modules;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.api.machines.ProcessType;
import sonar.calculator.mod.api.nutrition.IHealthStore;
import sonar.calculator.mod.utils.helpers.NutritionHelper;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class HealthModule extends SonarItem implements IHealthStore {

	public HealthModule() {
		this.setCreativeTab(Calculator.tab);
		this.maxStackSize = 1;
	}

	@Nonnull
    @Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		NutritionHelper.chargeHealth(stack, world, player, "points");
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Nonnull
    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {		
		ItemStack stack = player.getHeldItem(hand);
		NutritionHelper.useHealth(stack, player, world, pos, side, "points");
		return EnumActionResult.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
        super.addInformation(stack, world, list, par4);
		if (stack.hasTagCompound()) {
			list.add(FontHelper.translate("points.health") + ": " + getHealthPoints(stack));
		}
	}

	@Override
	public void transferHealth(int transfer, ItemStack stack, ProcessType process) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) {
			stack.setTagCompound((tag = new NBTTagCompound()));
		}
		tag.setInteger("points", 0);
		int points = tag.getInteger("points");
		if (process == ProcessType.REMOVE) {
			tag.setInteger("points", points - transfer);
		} else if (process == ProcessType.ADD) {
			tag.setInteger("points", points + transfer);
		}
	}

	@Override
	public int getHealthPoints(ItemStack stack) {
		return NutritionHelper.getIntegerTag(stack, "points");
	}

	@Override
	public int getMaxHealthPoints(ItemStack stack) {
		return CalculatorConfig.HEALTH_MODULE_CAPACITY;
	}

	@Override
	public void setHealth(ItemStack stack, int health) {
		if (!(health < 0) && health <= this.getMaxHealthPoints(stack)) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			NBTTagCompound nbtData = stack.getTagCompound();
			if (nbtData == null) {
				stack.getTagCompound().setInteger("points", 0);
			}
			nbtData.setInteger("points", health);
		}
	}
}
