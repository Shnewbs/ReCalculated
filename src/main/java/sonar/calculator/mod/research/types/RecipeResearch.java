package sonar.calculator.mod.research.types;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.recipes.ResearchRecipeType;
import sonar.calculator.mod.research.IResearch;
import sonar.calculator.mod.research.RecipeReward;
import sonar.calculator.mod.research.Research;
import sonar.calculator.mod.research.ResearchCategory;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.ArrayList;

public class RecipeResearch extends Research {

    public ArrayList<String> recipes = new ArrayList<>();

	public RecipeResearch() {
		super(ResearchTypes.RECIPES, "tile.ResearchChamber.name", Item.getItemFromBlock(Calculator.researchChamber));
	}

	@Override
	public String getHint() {
		return "Discover new recipes for the Calculator!";
	}

    @Override
	public ArrayList<RecipeReward> getUnlockedRecipes() {
        ArrayList<RecipeReward> unlocked = new ArrayList<>();
		unlocked.add(new RecipeReward("Calculator", recipes));
		return unlocked;
	}

	public void addRecipes(ArrayList<ResearchRecipeType> types) {
		for (ResearchRecipeType type : types) {
			if (!recipes.contains(type.name())) {
				recipes.add(type.name());
			}
		}
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		NBTTagCompound recipeList = (NBTTagCompound) nbt.getTag("recipeList");
        recipes = new ArrayList<>();
        recipes.addAll(recipeList.getKeySet());
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		NBTTagCompound recipeList = new NBTTagCompound();
		for (String id : recipes) {
			recipeList.setBoolean(id, true);
		}
		nbt.setTag("recipeList", recipeList);
		return nbt;
	}

	@Override
	public byte getProgress() {
        return (byte) (recipes.size() * 100 / ResearchRecipeType.values().length);
	}

	@Override
	public ResearchCategory getResearchType() {
		return ResearchCategory.RECIPES;
	}

	@Override
	public IResearch getInstance() {
		return new RecipeResearch();
	}
}
