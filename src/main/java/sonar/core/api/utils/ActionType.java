package sonar.core.api.utils;

/**
 * Enum representing the type of action, either simulating or performing the intended action.
 */
public enum ActionType {

	/**
	 * Simulates the intended action.
	 */
	SIMULATE,

	/**
	 * Performs the intended action.
	 */
	PERFORM;

	/**
	 * Checks if the action should simulate.
	 *
	 * @return True if the action is a simulation, false otherwise.
	 */
	public boolean shouldSimulate() {
		return this == SIMULATE;
	}

	/**
	 * Gets the ActionType based on whether the action should simulate or not.
	 *
	 * @param simulate True for SIMULATE, false for PERFORM.
	 * @return The corresponding ActionType.
	 */
	public static ActionType getTypeForAction(boolean simulate) {
		return simulate ? SIMULATE : PERFORM;
	}
}
