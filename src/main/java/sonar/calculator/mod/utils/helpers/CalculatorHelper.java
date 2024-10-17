package sonar.calculator.mod.utils.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.core.helpers.FontHelper;

import java.text.DecimalFormat;
import java.util.List;

public class CalculatorHelper {

	/** formatted Gas Levels */
	static DecimalFormat dec = new DecimalFormat("##.##");

	/** Adds stored energy to the Tool Tip
	 * @param stack Item that will feature list
	 * @param world The world
	 * @param list Tool Tip */
	public static void addEnergytoToolTip(ItemStack stack, World world, List<String> list) {
		if (stack.hasTagCompound()) {
			int energy = stack.getTagCompound().getInteger("energy");
			if (energy != 0) {
				list.add(FontHelper.translate("energy.stored") + ": " + FontHelper.formatStorage(energy));
			}
		}
	}

	/** Adds stored Item Level for Generators to the Tool Tip
	 * @param stack Item that will feature list
	 * @param world The world.
	 * @param list Tool Tip */
	public static void addItemLevelToolTip(ItemStack stack, World world, List<String> list) {
		if (stack.hasTagCompound()) {
			int standardLevel = stack.getTagCompound().getInteger("ItemLevel");
			int level = standardLevel * 100 / 5000;
			if (standardLevel != 0 && stack.getItem() == Item.getItemFromBlock(Calculator.starchextractor)) {
				String points = FontHelper.translate("generator.starch") + ": " + level + " %";
				list.add(points);
			} else if (standardLevel != 0 && stack.getItem() == Item.getItemFromBlock(Calculator.redstoneextractor)) {
				String points = FontHelper.translate("generator.redstone") + ": " + level + " %";
				list.add(points);
			} else if (standardLevel != 0 && stack.getItem() == Item.getItemFromBlock(Calculator.glowstoneextractor)) {
				String points = FontHelper.translate("generator.glowstone") + ": " + level + " %";
				list.add(points);
			}
		}
	}


	/** Adds stored Gas Level for Green Houses to the Tool Tip
	 * @param stack Item that will feature list
	 * @param world The world.
	 * @param list Tool Tip */
	public static void addGasToolTip(ItemStack stack, World world, List<String> list) {
		if (stack.hasTagCompound()) {
			int carbon = stack.getTagCompound().getInteger("Carbon");
			int oxygen = stack.getTagCompound().getInteger("Oxygen");
			if (carbon != 0) {
				String carbonString = FontHelper.translate("greenhouse.carbon") + ": " + dec.format(carbon * 100 / 100000) + '%';
				list.add(carbonString);
			}
			if (oxygen != 0) {
				String oxygenString = FontHelper.translate("greenhouse.oxygen") + ": " + dec.format(oxygen * 100 / 100000) + '%';
				list.add(oxygenString);
			}
		}
	}
}
