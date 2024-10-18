package sonar.core.integration.multipart;

import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;

public class TileInventoryMultipart extends TileSonarMultipart implements ISonarInventoryTile {

	protected ISonarInventory inventory;

	public TileInventoryMultipart() {}

	@Override
	public ISonarInventory inv() {
		return inventory;
	}

	// Optional: Initialize or manage your inventory here, if needed.
	public void setInventory(ISonarInventory inventory) {
		this.inventory = inventory;
	}

	// Optional: Add any additional methods needed to manage the inventory or interact with it.
}
