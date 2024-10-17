package sonar.calculator.mod.common.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.core.recipes.DefinedRecipeHelper;

public class StoneSeparatorRecipes extends DefinedRecipeHelper<CalculatorRecipe> {

	private static final StoneSeparatorRecipes recipes = new StoneSeparatorRecipes();

	public StoneSeparatorRecipes() {
		super(1, 2, false);
	}

    public static StoneSeparatorRecipes instance() {
		return recipes;
	}

	@Override
	public void addRecipes() {
		addRecipe("oreGold", new ItemStack(Calculator.enrichedgold_ingot, 4), new ItemStack(Calculator.small_stone, 2));
		addRecipe("oreIron", new ItemStack(Calculator.reinforcediron_ingot, 4), new ItemStack(Calculator.small_stone, 2));
		addRecipe("blockLapis", Calculator.large_amethyst, Calculator.shard_amethyst);
		addRecipe("gemLapis", Calculator.small_amethyst, Calculator.shard_amethyst);
		addRecipe(new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.PLANKS, 4, 0), new ItemStack(Items.STICK, 2, 0));
		addRecipe(new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.PLANKS, 4, 1), new ItemStack(Items.STICK, 2, 0));
		addRecipe(new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.PLANKS, 4, 2), new ItemStack(Items.STICK, 2, 0));
		addRecipe(new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.PLANKS, 4, 3), new ItemStack(Items.STICK, 2, 0));
		addRecipe(new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.PLANKS, 4, 4), new ItemStack(Items.STICK, 2, 0));
		addRecipe(new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.PLANKS, 4, 5), new ItemStack(Items.STICK, 2, 0));
		addRecipe(Calculator.amethystLeaves, new ItemStack(Blocks.LEAVES, 1, 0), new ItemStack(Calculator.shard_amethyst, 2));
		addRecipe(Calculator.amethystLog, new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Calculator.small_amethyst, 1));
	}

	@Override
	public String getRecipeID() {
		return "StoneSeparator";
	}
}
