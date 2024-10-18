package sonar.core.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class JEISonarPlugin implements IModPlugin {

    private final List<JEISonarProvider> providers = new ArrayList<>();

    public JEISonarProvider p(
            RecipeHelperV2 recipes,
            Object catalyst,
            Class<? extends IRecipeWrapper> recipeClass,
            JEISonarProvider.IRecipeFactory recipeFactory,
            JEISonarProvider.ICategoryFactory categoryFactory,
            String background,
            String modid) {

        JEISonarProvider provider = new JEISonarProvider(recipes, catalyst, recipeClass, recipeFactory, categoryFactory, background, modid);
        providers.add(provider);
        return provider;
    }

    public abstract void registerProviders();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registerProviders();
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        providers.forEach(provider ->
                registry.addRecipeCategories(provider.categoryFactory.create(guiHelper, provider))
        );
    }

    @Override
    public void register(IModRegistry registry) {
        providers.forEach(provider -> {
            registry.addRecipes(getJEIRecipes(provider), provider.recipes.getRecipeID());
            registry.addRecipeCatalyst(provider.catalyst, provider.recipes.getRecipeID());
        });
    }

    private Collection<JEISonarRecipe> getJEIRecipes(JEISonarProvider provider) {
        List<JEISonarRecipe> jeiRecipes = new ArrayList<>();
        for (Object recipe : provider.recipes.getRecipes()) {
            jeiRecipes.add(provider.recipeFactory.create(provider.recipes, (ISonarRecipe) recipe));
        }
        return jeiRecipes;
    }
}
