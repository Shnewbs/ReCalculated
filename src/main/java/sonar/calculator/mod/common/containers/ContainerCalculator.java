package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.recipes.CalculatorRecipes;
import sonar.calculator.mod.utils.SlotPortableCrafting;
import sonar.calculator.mod.utils.SlotPortableResult;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.common.item.InventoryItem;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.inventories.TransferSlotsManager;
import sonar.core.handlers.inventories.containers.ContainerSonar;
import sonar.core.recipes.RecipeHelperV2;

import javax.annotation.Nonnull;

public class ContainerCalculator extends ContainerSonar implements ICalculatorCrafter {

	public static TransferSlotsManager<InventoryItem> TRANSFER = new TransferSlotsManager(3);
	private final InventoryItem inventory;

	private EntityPlayer player;

	public ContainerCalculator(EntityPlayer player, InventoryItem inventoryItem) {
		this.player = player;
		this.inventory = inventoryItem;
		addSlotToContainer(new SlotPortableCrafting(this, inventory, 0, 25, 35, Calculator.itemCalculator));
		addSlotToContainer(new SlotPortableCrafting(this, inventory, 1, 79, 35, Calculator.itemCalculator));
		addSlotToContainer(new SlotPortableResult(player, inventory, this, new int[] { 0, 1 }, 2, 134, 35));
		addInventoryWithLimiter(player.inventory, 8, 84, Calculator.itemCalculator);
		onItemCrafted();
	}

	@Override
	public void onItemCrafted() {
		inventory.setInventorySlotContents(2, RecipeHelperV2.getItemStackFromList(CalculatorRecipes.instance().getOutputs(player, inventory.getStackInSlot(0), inventory.getStackInSlot(1)), 0));
	}

    @Override
	public void removeEnergy(int remove) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		EnergyTransferHandler.INSTANCE_SC.dischargeItem(player.getHeldItemMainhand(), remove, EnergyType.FE, ActionType.PERFORM);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUsableByPlayer(player);
	}

    @Nonnull
    @Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return TRANSFER.transferStackInSlot(this, inventory, player, slotID);
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
