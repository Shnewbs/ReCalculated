package sonar.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public final class SonarButtons {

	public static class SonarButton extends Button {

		public boolean isButtonDown;

		public SonarButton(int id, int x, int y, int width, int height, String display, Button.IPressable pressable) {
			super(x, y, width, height, display, pressable);
		}

		@Override
		public boolean mouseClicked(double x, double y, int button) {
			isButtonDown = this.active && this.visible && x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
			return super.mouseClicked(x, y, button);
		}

		@Override
		public void mouseReleased(double x, double y) {
			isButtonDown = false;
			super.mouseReleased(x, y);
		}
	}

	public static class HoverButton extends SonarButton {

		public HoverButton(int id, int x, int y, int width, int height, String display, Button.IPressable pressable) {
			super(id, x, y, width, height, display, pressable);
		}

		public String getHoverText() {
			return "";
		}
	}

	public static abstract class ImageButton extends SonarButton {
		public final ResourceLocation texture;
		protected int textureX;
		protected int textureY;
		protected int sizeX, sizeY;

		protected ImageButton(int id, int x, int y, ResourceLocation texture, int textureX, int textureY, int sizeX, int sizeY, Button.IPressable pressable) {
			super(id, x, y, sizeX, sizeY, "", pressable);
			this.texture = texture;
			this.textureX = textureX;
			this.textureY = textureY;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}

		@Override
		public void renderButton(@Nonnull Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				this.isHovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
				mc.getTextureManager().bind(texture);
				blit(this.x, this.y, this.textureX, this.textureY, sizeX, sizeY, 256, 256);
			}
		}
	}

	public static abstract class AnimatedButton extends SonarButton {
		protected final ResourceLocation texture;
		protected final int sizeX, sizeY;

		protected AnimatedButton(int id, int x, int y, ResourceLocation texture, int sizeX, int sizeY, Button.IPressable pressable) {
			super(id, x, y, sizeX, sizeY, "", pressable);
			this.texture = texture;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}

		public abstract int getTextureX();

		public abstract int getTextureY();

		@Override
		public void renderButton(@Nonnull Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				this.isHovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
				mc.getTextureManager().bind(texture);
				blit(this.x, this.y, getTextureX(), getTextureY(), sizeX, sizeY, 256, 256);
			}
		}
	}
}
