package sonar.core.client.gui;

import java.io.IOException;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.systems.RenderSystem;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import sonar.core.client.gui.widgets.SonarScroller;

public abstract class GuiSelectionGrid<T> extends ContainerScreen<Container> {

	public int yPos = 32;
	public int xPos = 13;
	public int eWidth = 18;
	public int eHeight = 18;
	public int gWidth = 12;
	public int gHeight = 7;
	public SonarScroller scroller;
	public SelectionGrid<T> grid;

	public GuiSelectionGrid(Container container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
		this.xSize = 256;
		this.ySize = 256;
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scroller = new SonarScroller(this.guiLeft + xPos + (eWidth * gWidth) + 3, this.guiTop + yPos - 1, (eHeight * gHeight) + 2, 10);
		grid = new SelectionGrid<>(this, 0, xPos, yPos, eWidth, eHeight, gWidth, gHeight);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (super.mouseClicked(x, y, button)) {
			return true;
		}
		if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
			grid.mouseClicked(this, (int) x, (int) y, button);
		}
		return false;
	}

	@Override
	protected void renderForeground(int mouseX, int mouseY) {
		super.renderForeground(mouseX, mouseY);
		renderStrings(mouseX, mouseY);
		grid.renderGrid(this, mouseX, mouseY);
	}

	public void startToolTipRender(T selection, int x, int y) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableLighting();
		this.renderElementToolTip(selection, x, y);
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();
		net.minecraft.client.renderer.RenderHelper.setupFor3DItems();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroller.handleMouse(grid);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		grid.setList(Lists.newArrayList(this.getGridList()));
		scroller.drawScreen(mouseX, mouseY, grid.isScrollable());
	}

	@Override
	protected void renderBackground(float partialTicks, int mouseX, int mouseY) {
		super.renderBackground(partialTicks, mouseX, mouseY);
		// Uncomment and adjust rendering code if needed for drawing background layers.
	}

	public abstract void onGridClicked(T element, int x, int y, int pos, int button, boolean empty);

	public abstract void renderGridElement(T element, int x, int y, int slot);

	public abstract void renderStrings(int x, int y);

	public abstract void renderElementToolTip(T element, int x, int y);

	public abstract List<T> getGridList();

	public void preRender() {}

	public void postRender() {}

	public static class SelectionGrid<T> extends GuiGridElement<T> {
		public GuiSelectionGrid<T> selectGrid;

		public SelectionGrid(GuiSelectionGrid<T> selectGrid, int gridID, int yPos, int xPos, int eWidth, int eHeight, int gWidth, int gHeight) {
			super(gridID, yPos, xPos, eWidth, eHeight, gWidth, gHeight);
			this.selectGrid = selectGrid;
		}

		@Override
		public float getCurrentScroll() {
			return selectGrid.scroller.getCurrentScroll();
		}

		@Override
		public void onGridClicked(T selection, int x, int y, int pos, int button, boolean empty) {
			selectGrid.onGridClicked(selection, x, y, pos, button, empty);
		}

		@Override
		public void renderGridElement(T selection, int x, int y, int slot) {
			selectGrid.renderGridElement(selection, x, y, slot);
		}

		@Override
		public void renderElementToolTip(T selection, int x, int y) {
			selectGrid.startToolTipRender(selection, x, y);
		}

		public void preRender() {
			selectGrid.preRender();
			RenderSystem.enableDepthTest();
			net.minecraft.client.renderer.RenderHelper.setupFor3DItems();
		}

		public void postRender() {
			selectGrid.postRender();
		}
	}
}