package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.recipes.FlawlessCalculatorRecipes;
import sonar.calculator.mod.utils.SlotPortableCrafting;
import sonar.calculator.mod.utils.SlotPortableResult;
import sonar.core.common.item.InventoryItem;
import sonar.core.handlers.inventories.TransferSlotsManager;
import sonar.core.handlers.inventories.containers.ContainerSonar;
import sonar.core.recipes.RecipeHelperV2;

import javax.annotation.Nonnull;

public class ContainerFlawlessCalculator extends ContainerSonar implements ICalculatorCrafter {
	public final InventoryItem inventory;
	public static TransferSlotsManager<InventoryItem> transfer = new TransferSlotsManager<InventoryItem>() {
		{
			addTransferSlot(new TransferSlots<InventoryItem>(TransferType.TILE_INV, 4));
			addTransferSlot(new DisabledSlots<InventoryItem>(TransferType.TILE_INV, 1));
			addPlayerInventory();
		}
	};
	public final EntityPlayer player;

	public ContainerFlawlessCalculator(EntityPlayer player, InventoryItem inventoryItem) {
		this.inventory = inventoryItem;
		this.player = player;

		for (int k = 0; k < 4; k++) {
			addSlotToContainer(new SlotPortableCrafting(this, inventory, k, 17 + k * 32, 35, Calculator.itemFlawlessCalculator));
		}

		addSlotToContainer(new SlotPortableResult(player, inventory, this, new int[] { 0, 1, 2, 3 }, 4, 145, 35));
		addInventoryWithLimiter(player.inventory, 8, 84, Calculator.itemFlawlessCalculator);
		onItemCrafted();
	}

	@Override
	public void onItemCrafted() {
        inventory.setInventorySlotContents(4, RecipeHelperV2.getItemStackFromList(FlawlessCalculatorRecipes.instance().getOutputs(player, inventory.getStackInSlot(0), inventory.getStackInSlot(1), inventory.getStackInSlot(2), inventory.getStackInSlot(3)), 0), false);
	}

    @Override
    public void removeEnergy(int remove) {

    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUsableByPlayer(player);
	}

	@Nonnull
    @Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return transfer.transferStackInSlot(this, inventory, player, slotID);
	}

	@Nonnull
    @Override
	public ItemStack slotClick(int slot, int drag, ClickType click, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, drag, click, player);
	}
}
