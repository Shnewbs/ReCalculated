package sonar.core.api.energy;

/**
 * Enum representing different energy modes for blocks/entities.
 * Defines behaviors for sending, receiving, or blocking energy transfer.
 */
public enum EnergyMode {
	RECIEVE, SEND, SEND_RECIEVE, BLOCKED;

	/**
	 * @return true if the mode allows sending energy.
	 */
	public boolean canSend() {
		return this == SEND || this == SEND_RECIEVE;
	}

	/**
	 * @return true if the mode allows receiving energy.
	 */
	public boolean canRecieve() {
		return this == RECIEVE || this == SEND_RECIEVE;
	}

	/**
	 * @return true if energy connections are allowed (not blocked).
	 */
	public boolean canConnect() {
		return this != BLOCKED;
	}
}
