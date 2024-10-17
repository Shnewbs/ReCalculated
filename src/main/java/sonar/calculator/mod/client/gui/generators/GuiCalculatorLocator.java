package sonar.calculator.mod.client.gui.generators;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import sonar.calculator.mod.common.containers.ContainerCalculatorLocator;
import sonar.calculator.mod.common.tileentity.generators.TileEntityCalculatorLocator;
import sonar.core.client.gui.GuiSonarTile;
import sonar.core.helpers.FontHelper;

public class GuiCalculatorLocator extends GuiSonarTile {
	
	public static final ResourceLocation bground = new ResourceLocation("Calculator:textures/gui/guicalculatorlocator.png");
	public TileEntityCalculatorLocator entity;
	
	public GuiCalculatorLocator(InventoryPlayer inventoryPlayer, TileEntityCalculatorLocator entity) {
		super(new ContainerCalculatorLocator(inventoryPlayer, entity), entity);
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int size = entity.size.getObject();
		int stability = entity.stability.getObject();
		FontHelper.text(FontHelper.translate("locator.active") + ": " + (entity.active.getObject() ? FontHelper.formatOutput(entity.currentGen.getObject()) : FontHelper.translate("locator.false")), 25, 10, entity.active.getObject() ? 0 : 2);
		FontHelper.text(FontHelper.translate("locator.multiblock") + ": " + (size != 0 ? FontHelper.translate("locator.true") : FontHelper.translate("locator.false")), 25, 21, size != 0 ? 0 : 2);
		FontHelper.text(FontHelper.translate("locator.owner") + ": " + (!entity.owner.equals("None") ? entity.getOwner() : FontHelper.translate("locator.none")), 25, 32, !this.entity.owner.equals("None") ? 0 : 2);
        FontHelper.text(FontHelper.translate("circuit.stability") + ": " + (size != 0 ? stability == 0 ? 0 + "%" : String.valueOf(entity.getStabilityPercent()) + '%' : FontHelper.translate("locator.unknown")), 25, 43, !(stability == 0 || size == 0) ? 0 : 2);
		FontHelper.textCentre(FontHelper.formatStorage(entity.storage.getEnergyLevel()), xSize, 64, 2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		int k = (int)(this.entity.storage.getEnergyLevel() * 78 / entity.storage.getFullCapacity());
		int j = 78 - k;
		drawTexturedModalRect(this.guiLeft + 49, this.guiTop + 63, 176, 0, k, 10);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}
}