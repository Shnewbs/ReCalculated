package sonar.core.integration.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeItemStack;
import sonar.core.recipes.RecipeOreStack;

import javax.annotation.Nullable;

public class CraftTweakerHelper {

    @Nullable
    public static ISonarRecipeObject convertItemIngredient(IIngredient ingredient) {
        if (ingredient.getItems().size() > 0) {
            if (ingredient instanceof IOreDictEntry) {
                IOreDictEntry oreDict = (IOreDictEntry) ingredient;
                return new RecipeOreStack(oreDict.getName(), oreDict.getAmount());
            }
            return new RecipeItemStack(CraftTweakerMC.getItemStack(ingredient), false);
        }
        return null;
    }
}
