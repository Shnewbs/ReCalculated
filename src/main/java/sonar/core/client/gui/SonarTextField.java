package sonar.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

public class SonarTextField extends EditBox {

	protected String defString = "";
	protected boolean digitsOnly;
	protected int outlineColour = 0xFFA0A0A0, boxColour = 0xFF000000;

	public SonarTextField(Font renderer, int x, int y, int width, int height, Component message) {
		super(renderer, x, y, width, height, message);
	}

	public void setDefault(String defString) {
		this.defString = defString;
	}

	public SonarTextField setDigitsOnly(boolean digitsOnly) {
		this.digitsOnly = digitsOnly;
		return this;
	}

	public SonarTextField setBoxOutlineColour(int outlineColour) {
		this.outlineColour = outlineColour;
		return this;
	}

	public SonarTextField setBoxColour(int boxColour) {
		this.boxColour = boxColour;
		return this;
	}

	@Override
	public boolean charTyped(char c, int modifiers) {
		if (digitsOnly) {
			if (Character.isDigit(c)) {
				return super.charTyped(c, modifiers);
			}
			return false;
		}
		return super.charTyped(c, modifiers);
	}

	public int getIntegerFromText() {
		try {
			return Integer.parseInt(getValue().isEmpty() ? "0" : getValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public long getLongFromText() {
		try {
			return Long.parseLong(getValue().isEmpty() ? "0" : getValue());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.isVisible()) {
			if (this.isBordered()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, outlineColour);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, boxColour);
			}

			int i = this.isEditable() ? this.textColor : this.textColorUneditable;
			int j = this.getCursorPosition() - this.displayPos;
			int k = this.highlightPos - this.displayPos;
			String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
			boolean flag = j >= 0 && j <= s.length();
			boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
			int l = this.x + 4;
			int i1 = this.y + (this.height - 8) / 2;
			int j1 = l;

			if (k > s.length()) {
				k = s.length();
			}

			if (!s.isEmpty()) {
				String s1 = flag ? s.substring(0, j) : s;
				j1 = this.font.drawShadow(this.font.isBidirectional() ? new StringBuilder(s1).reverse().toString() : s1, (float) l, (float) i1, i);
			}

			boolean flag2 = this.getCursorPosition() < this.value.length() || this.value.length() >= this.getMaxLength();
			int k1 = j1;

			if (!flag) {
				k1 = j < 0 ? l : j1 - 1;
			} else if (flag1) {
				fill(k1, i1 - 1, k1 + 1, i1 + 9, i);
			}

			if (k != j) {
				int l1 = l + this.font.width(s.substring(0, k));
				this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 9);
			}
		}
	}
}
