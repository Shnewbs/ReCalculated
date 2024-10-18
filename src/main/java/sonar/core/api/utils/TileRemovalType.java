package sonar.core.api.utils;

/**
 * Enum for defining types of tile removal, such as chunk unloading or full removal.
 */
public enum TileRemovalType {
	CHUNK_UNLOAD, REMOVE;

	/**
	 * @return True if the removal type is a full removal.
	 */
	public boolean isFullRemoval() {
		return this == REMOVE;
	}
}
