package sonar.calculator.mod.common.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.core.recipes.DefinedRecipeHelper;

public class AlgorithmSeparatorRecipes extends DefinedRecipeHelper<CalculatorRecipe> {

	private static final AlgorithmSeparatorRecipes recipes = new AlgorithmSeparatorRecipes();

	public AlgorithmSeparatorRecipes() {
		super(1, 2, false);
	}

    public static AlgorithmSeparatorRecipes instance() {
		return recipes;
	}

	@Override
	public void addRecipes() {
		addRecipe("blockLapis", Calculator.large_tanzanite, Calculator.shard_tanzanite);
		addRecipe("gemLapis", Calculator.small_tanzanite, Calculator.shard_tanzanite);
		addRecipe("gemDiamond", new ItemStack(Calculator.weakeneddiamond, 4), new ItemStack(Items.DYE, 2, 4));
		addRecipe("dustRedstone", new ItemStack(Calculator.redstone_ingot, 2), new ItemStack(Calculator.small_stone, 2));
		addRecipe(Calculator.tanzaniteLeaves, new ItemStack(Blocks.LEAVES, 1), new ItemStack(Calculator.shard_tanzanite, 2));
		addRecipe(Calculator.tanzaniteLog, new ItemStack(Blocks.LOG, 1), Calculator.small_tanzanite);
	}

	@Override
	public String getRecipeID() {
		return "AlgorithmSeparator";
	}
}
