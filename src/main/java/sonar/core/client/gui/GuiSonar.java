package sonar.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.client.gui.screen.inventory.ContainerScreen;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiSonar extends ContainerScreen<Container> implements IGuiOrigin {

	protected List<SonarTextField> fieldList = new ArrayList<>();
	private boolean shouldReset = false;

	/// the gui which opened this one, generally null.
	public Object origin;

	public GuiSonar(Container container, PlayerInventory inventory, Text title) {
		super(container, inventory, title);
	}

	public abstract Identifier getBackground();

	//// RENDER METHODS \\\\n
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (shouldReset) {
			doReset();
		}
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		super.drawForeground(matrices, mouseX, mouseY);
		fieldList.forEach(field -> field.render(matrices));
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(this.getBackground());
		DrawableHelper.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	protected void renderTooltip(MatrixStack matrices, int x, int y) {
		super.renderTooltip(matrices, x, y);
		for (ButtonWidget button : this.buttons) {
			if (button.isHovered()) {
				button.renderTooltip(matrices, x, y);
				break;
			}
		}
	}

	//// STANDARD EVENTS \\\

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		for (SonarTextField field : fieldList) {
			boolean focused = field.mouseClicked(mouseX - x, mouseY - y, button);
			if (focused) {
				onTextFieldFocused(field);
			}
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (SonarTextField field : fieldList) {
			if (field.isFocused()) {
				if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
					field.setFocused(false);
				} else {
					field.keyPressed(keyCode, scanCode, modifiers);
					onTextFieldChanged(field);
				}
				return true;
			}
		}
		if (isCloseKey(keyCode) && origin != null) {
			Minecraft.getInstance().openScreen((Screen) origin);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onTextFieldChanged(SonarTextField field) {}

	public void onTextFieldFocused(SonarTextField field) {}

	public void reset() {
		shouldReset = true;
	}

	public void doReset() {
		this.buttons.clear();
		this.fieldList.clear();
		this.init();
		shouldReset = false;
	}

	public void initButtons() {
		this.buttons.clear();
	}

	public SonarTextField getFocusedField() {
		for (SonarTextField f : fieldList) {
			if (f.isFocused()) {
				return f;
			}
		}
		return null;
	}

	public void bindTexture(Identifier resource) {
		Minecraft.getInstance().getTextureManager().bindTexture(resource);
	}

	public void setOrigin(Object origin) {
		this.origin = origin;
	}

	public void setZLevel(float zLevel) {
		this.setZOffset((int) zLevel);
	}

	public boolean isCloseKey(int keyCode) {
		return keyCode == GLFW.GLFW_KEY_ESCAPE || this.client.options.keyInventory.matchesKey(keyCode, 0);
	}

	@Override
	public int getGuiLeft() {
		return x;
	}

	@Override
	public int getGuiTop() {
		return y;
	}

	//// RENDER METHODS \\\

	public void drawSonarCreativeTabHoveringText(String tabName, MatrixStack matrices, int mouseX, int mouseY) {
		renderTooltip(matrices, Lists.newArrayList(tabName), mouseX, mouseY);
	}

	public void drawSonarCreativeTabHoveringText(List<String> text, MatrixStack matrices, int mouseX, int mouseY) {
		renderTooltip(matrices, text, mouseX, mouseY);
	}

	public void startNormalItemStackRender() {
		RenderSystem.enableDepthTest();
		RenderHelper.setupFor3DItems();
		RenderSystem.translatef(0.0F, 0.0F, 32.0F);
		this.setZOffset(200);
	}

	public void drawNormalItemStack(ItemStack stack, int x, int y) {
		startNormalItemStackRender();
		TextRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = textRenderer;
		this.itemRenderer.renderInGuiWithOverrides(stack, x, y);
		this.itemRenderer.renderGuiItemOverlay(font, stack, x, y, "");
		finishNormalItemStackRender();
	}

	public void finishNormalItemStackRender() {
		this.setZOffset(0);
		RenderHelper.setupForFlatItems();
		RenderSystem.disableDepthTest();
	}

	public void drawNormalToolTip(ItemStack stack, MatrixStack matrices, int x, int y) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableLighting();
		this.renderTooltip(matrices, stack, x - this.x, y - this.y);
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();
		net.minecraft.client.render.item.ItemRenderer.renderGuiItemModel();
	}

	public void drawSpecialToolTip(List<String> list, MatrixStack matrices, int x, int y, TextRenderer font) {
		RenderSystem.disableDepthTest();
		RenderSystem.disableLighting();
		renderTooltip(matrices, list, x - this.x, y - this.y, font == null ? this.textRenderer : font);
		RenderSystem.enableLighting();
		RenderSystem.enableDepthTest();
		net.minecraft.client.render.item.ItemRenderer.renderGuiItemModel();
	}

	public static void drawTransparentRect(MatrixStack matrices, int left, int top, int right, int bottom, int color) {
		if (left < right) {
			int i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			int j = top;
			top = bottom;
			bottom = j;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(f, f1, f2, f3);
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(f, f1, f2, f3);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
		vertexbuffer.vertex((double) left, (double) bottom, 0.0D).next();
		vertexbuffer.vertex((double) right, (double) bottom, 0.0D).next();
		vertexbuffer.vertex((double) right, (double) top, 0.0D).next();
		vertexbuffer.vertex((double) left, (double) top, 0.0D).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
