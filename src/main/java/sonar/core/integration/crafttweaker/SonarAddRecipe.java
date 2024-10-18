package sonar.core.integration.crafttweaker;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.ValueHelperV2;

import java.util.ArrayList;
import java.util.List;

public class SonarAddRecipe<HELPER extends RecipeHelperV2> implements IAction {

    private final HELPER recipes;
    private final List<ISonarRecipeObject> sonarInputs = new ArrayList<>();
    private final List<ISonarRecipeObject> sonarOutputs = new ArrayList<>();
    private boolean valid = true;

    public SonarAddRecipe(HELPER recipes, List<IIngredient> inputs, List<IIngredient> outputs) {
        this.recipes = recipes;
        for (IIngredient ingredient : inputs) {
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(ingredient);
            if (obj == null) {
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID INPUT: %s", recipes.getRecipeID(), ingredient));
            } else {
                sonarInputs.add(obj);
            }
        }
        for (IIngredient ingredient : outputs) {
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(ingredient);
            if (obj == null) {
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID OUTPUT: %s", recipes.getRecipeID(), ingredient));
            } else {
                sonarOutputs.add(obj);
            }
        }
    }

    @Override
    public void apply() {
        if (valid) {
            boolean isShapeless = !(recipes instanceof DefinedRecipeHelper) || ((DefinedRecipeHelper) recipes).shapeless;
            recipes.addRecipe(recipes.buildRecipe(sonarInputs, sonarOutputs, new ArrayList<>(), isShapeless));
        }
    }

    @Override
    public String describe() {
        return String.format("Adding %s recipe (%s = %s)", recipes.getRecipeID(),
                RecipeHelperV2.getValuesFromList(sonarInputs), RecipeHelperV2.getValuesFromList(sonarOutputs));
    }

    public static class Value extends SonarAddRecipe<ValueHelperV2> {

        private final int recipeValue;

        public Value(ValueHelperV2 helper, List<IIngredient> inputs, List<IIngredient> outputs, int recipeValue) {
            super(helper, inputs, outputs);
            this.recipeValue = recipeValue;
        }

        @Override
        public void apply() {
            if (valid) {
                recipes.addRecipe(recipes.buildRecipe(sonarInputs, sonarOutputs, Lists.newArrayList(recipeValue), recipes.shapeless));
            }
        }
    }
}
