package sonar.core.api;

/**
 * Interface for containers that can handle click events.
 */
public interface IClickableContainer {

	/**
	 * Called when the container is clicked.
	 *
	 * @param id The id of the click event.
	 */
	void onClick(int id);
}
