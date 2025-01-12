package sonar.core.client.gui;

import java.util.List;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;

public class HelpOverlay<G extends GuiSonarTile> {

	public final List<String> description;
	public final int left, top, width, height, colour;

	public HelpOverlay(String key, int left, int top, int width, int height, int colour) {
		this.description = SonarHelper.convertArray(FontHelper.translate(key).split("-"));
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.colour = colour;
	}

	public boolean isCompletedSuccess(G gui) {
		return false;
	}

	public boolean canBeRendered(G gui) {
		return false;
	}
}
