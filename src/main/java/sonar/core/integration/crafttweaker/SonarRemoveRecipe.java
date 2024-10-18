package sonar.core.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.RecipeObjectType;

import java.util.ArrayList;
import java.util.List;

public class SonarRemoveRecipe implements IAction {

    private final RecipeHelperV2 recipes;
    private ISonarRecipe recipe;
    private boolean valid = true;

    public SonarRemoveRecipe(RecipeHelperV2 recipes, RecipeObjectType type, List<IIngredient> ingredients) {
        this.recipes = recipes;
        List<ISonarRecipeObject> sonarObjects = new ArrayList<>();

        for (IIngredient ingredient : ingredients) {
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(ingredient);
            if (obj == null) {
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID ITEM: %s", recipes.getRecipeID(), ingredient));
            } else {
                sonarObjects.add(obj);
            }
        }

        if (valid) {
            switch (type) {
                case INPUT:
                    this.recipe = recipes.getRecipeFromInputs(null, sonarObjects.toArray());
                    break;
                case OUTPUT:
                    this.recipe = recipes.getRecipeFromOutputs(null, sonarObjects.toArray());
                    break;
            }
        }
    }

    @Override
    public void apply() {
        if (valid && recipe != null) {
            boolean result = recipes.removeRecipe(recipe);
            if (!result) {
                CraftTweakerAPI.logError(String.format("%s: Removing Recipe - Failed to remove recipe %s", recipes.getRecipeID(), recipe));
            }
        }
    }

    @Override
    public String describe() {
        if (!valid || recipe == null) {
            return "INVALID RECIPE";
        }
        return String.format("Removing %s recipe (%s = %s)", recipes.getRecipeID(), recipe.inputs(), recipe.outputs());
    }
}
