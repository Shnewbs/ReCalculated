package sonar.core.client.gui;

import net.minecraft.inventory.container.Container;
import sonar.core.utils.IWorldPosition;

public abstract class GuiSonarTile extends GuiSonar {

	public IWorldPosition entity;

	public GuiSonarTile(Container container, IWorldPosition entity) {
		super(container);
		this.entity = entity;
	}
}
