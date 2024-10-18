package sonar.core.api.blocks;

/**
 * Interface used on blocks that can connect to other connected block types.
 */
public interface IConnectedBlock {

    /**
     * @return an array representing possible connection types.
     * Refer to ConnectedBlock for the connection type definitions.
     */
    int[] getConnections();
}
