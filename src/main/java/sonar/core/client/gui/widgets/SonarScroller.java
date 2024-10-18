package sonar.core.client.gui.widgets;

import org.lwjgl.input.Mouse;
import sonar.core.client.gui.GuiGridElement;

/**
 * SonarScroller handles scroll bar rendering and mouse interactions, with support for vertical and horizontal orientations.
 */
public class SonarScroller {

	public boolean renderScroller = true;
	public float currentScroll;
	public boolean isScrolling;
	public boolean wasClicking;
	public int left, top, length, width;
	public ScrollerOrientation orientation = ScrollerOrientation.VERTICAL;

	public SonarScroller(int scrollerLeft, int scrollerStart, int length, int width) {
		this.left = scrollerLeft;
		this.top = scrollerStart;
		this.length = length;
		this.width = width;
	}

	public SonarScroller setOrientation(ScrollerOrientation orientation) {
		this.orientation = orientation;
		return this;
	}

	public float getCurrentScroll() {
		return currentScroll;
	}

	public void handleMouse(GuiGridElement grid) {
		handleMouse(grid.isScrollable(), grid.getGridSize());
	}

	public void handleMouse(boolean needsScrollBars, int listSize) {
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && needsScrollBars) {
			int j = listSize + 1;
			i = i > 0 ? 1 : -1;
			currentScroll = (float) ((double) currentScroll - (double) i / (double) j);
			currentScroll = Math.max(0.0F, Math.min(1.0F, currentScroll));
		}
	}

	public void drawScreen(int x, int y, boolean needsScrollBars) {
		boolean flag = Mouse.isButtonDown(0);
		if (!this.wasClicking && flag && x >= left && y >= top && x < left + width && y < top + length) {
			this.isScrolling = needsScrollBars;
		}
		if (!flag) {
			this.isScrolling = false;
		}
		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = orientation.isVertical() ? ((float) (y - top) - 7.5F) / ((float) length - 15.0F) : ((float) (x - left) - 7.5F) / ((float) width - 15.0F);
			currentScroll = Math.max(0.0F, Math.min(1.0F, currentScroll));
		}
	}
}
