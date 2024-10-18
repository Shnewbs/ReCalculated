package sonar.core.api.cabling;

import sonar.core.api.utils.BlockCoords;

/**
 * Interface representing a Sonar cable with coordinates and registry ID.
 */
public interface ISonarCable {

    /**
     * @return The coordinates of the Sonar cable block.
     */
    BlockCoords getCoords();

    /**
     * @return The registry ID associated with this cable.
     */
    int registryID();
}
