package sonar.core.api.upgrades;

/**
 * Interface for TileEntities that can be upgraded.
 */
public interface IUpgradableTile {

    /**
     * @return The upgrade inventory for the tile.
     */
    IUpgradeInventory getUpgradeInventory();
}
