package sonar.calculator.mod.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.common.recipes.CalculatorRecipes;
import sonar.calculator.mod.common.tileentity.machines.TileEntityResearchChamber;

public class SlotResearch extends Slot {
	private EntityPlayer player;
	private TileEntityResearchChamber entity;

	public SlotResearch(TileEntityResearchChamber entity, int index, int x, int y, EntityPlayer player) {
		super(entity, index, x, y);
		this.player = player;
		this.entity = entity;
	}

    @Override
	public void onSlotChange(ItemStack stack1, ItemStack stack2) {
		super.onSlotChange(stack1, stack2);
		if (player != null) {
			/*
			int[] unblocked = entity.unblockedList();
			if (entity.isBlocked(entity.lastResearch) && entity.lastResearch != 0) {
				if (CalculatorRecipes.recipes().discovery(unblocked, entity.lastResearch)) {
					FontHelper.sendMessage(FontHelper.translate("research.recipeNew"), entity.getWorld(), player);
				}
			}
			*/
			entity.markBlockForUpdate();
			entity.getWorld().addBlockEvent(entity.getPos(), entity.getBlockType(), 1, 0);
		}
	}

    @Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if (player != null) {
			/*
			int[] unblocked = entity.unblockedList();
			if (entity.isBlocked(entity.lastResearch) && entity.lastResearch != 0) {
				if (CalculatorRecipes.recipes().discovery(unblocked, entity.lastResearch)) {
					FontHelper.sendMessage(FontHelper.translate("research.recipeNew"), entity.getWorld(), player);
				}
			}
			*/
			entity.markBlockForUpdate();
			entity.getWorld().addBlockEvent(entity.getPos(), entity.getBlockType(), 1, 0);
		}
	}

    @Override
	public boolean isItemValid(ItemStack stack) {
		return CalculatorRecipes.instance().isValidInput(stack);
	}
}
