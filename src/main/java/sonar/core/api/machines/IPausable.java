package sonar.core.api.machines;

/**
 * Interface for machines that can be paused or stopped altogether.
 */
public interface IPausable {

    /**
     * Called when the machine is paused or resumed.
     */
    void onPause();

    /**
     * @return true if the machine is currently active and running.
     */
    boolean isActive();

    /**
     * @return true if the machine is currently paused.
     */
    boolean isPaused();
}
