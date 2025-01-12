package sonar.core.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class JEISonarRecipe<T extends JEISonarRecipe<T>> implements IRecipeWrapper {

	protected final RecipeHelperV2 helper;
	protected final ISonarRecipe recipe;
	protected final List<List<ItemStack>> inputs;
	protected final List<ItemStack> outputs;

	public JEISonarRecipe(RecipeHelperV2 helper, ISonarRecipe recipe) {
		this.helper = helper;
		this.recipe = recipe;
		this.inputs = RecipeHelperV2.getJEIInputsFromList(recipe.inputs());
		this.outputs = RecipeHelperV2.getJEIOutputsFromList(recipe.outputs());
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

}
