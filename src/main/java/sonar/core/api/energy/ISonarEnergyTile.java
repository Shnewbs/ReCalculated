package sonar.core.api.energy;

import net.minecraft.util.EnumFacing;
import sonar.core.network.sync.SyncEnergyStorage;

/**
 * Interface for energy tiles handling energy modes and storage.
 */
public interface ISonarEnergyTile {

    /**
     * Retrieves the energy mode for a given side of the tile.
     *
     * @param side The side of the tile to check.
     * @return The energy mode for that side.
     */
    EnergyMode getModeForSide(EnumFacing side);

    /**
     * @return The synchronized energy storage for this tile.
     */
    SyncEnergyStorage getStorage();

    /**
     * @return The default energy mode of the tile.
     */
    EnergyMode getMode();
}
