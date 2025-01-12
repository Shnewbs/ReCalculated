package sonar.core.integration.jei;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.RecipeObjectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JEISonarMapper {

	private final Map<RecipeObjectType, Map<Integer, RecipeMapping>> map = new HashMap<>();

	public JEISonarMapper() {}

	public void map(RecipeObjectType type, int recipePos, int slotPos, int xPos, int yPos) {
		this.map(type, recipePos, new RecipeMapping(slotPos, xPos, yPos));
	}

	public void map(RecipeObjectType type, int recipePos, RecipeMapping mapping) {
		map.computeIfAbsent(type, k -> new HashMap<>()).put(recipePos, mapping);
	}

	public void mapTo(IGuiItemStackGroup stacks, IIngredients ingredients) {
		for (Entry<RecipeObjectType, Map<Integer, RecipeMapping>> entry : map.entrySet()) {
			List<List<ItemStack>> objects = entry.getKey() == RecipeObjectType.INPUT
					? ingredients.getInputs(ItemStack.class)
					: ingredients.getOutputs(ItemStack.class);

			for (Entry<Integer, RecipeMapping> mapping : entry.getValue().entrySet()) {
				RecipeMapping recipe = mapping.getValue();
				stacks.init(recipe.slotPos, entry.getKey() == RecipeObjectType.INPUT, recipe.xPos, recipe.yPos);

				List<ItemStack> obj = objects.get(mapping.getKey());
				stacks.set(recipe.slotPos, obj != null ? obj : List.of(ItemStack.EMPTY));
			}
		}
	}

	private static class RecipeMapping {
		private final int slotPos;
		private final int xPos;
		private final int yPos;

		private RecipeMapping(int slotPos, int xPos, int yPos) {
			this.slotPos = slotPos;
			this.xPos = xPos;
			this.yPos = yPos;
		}
	}
}
