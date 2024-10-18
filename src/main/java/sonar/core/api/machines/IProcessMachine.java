package sonar.core.api.machines;

/**
 * Interface for machines that have progress bars and process times.
 */
public interface IProcessMachine {

    /**
     * @return The current process time of the machine.
     */
    int getCurrentProcessTime();

    /**
     * @return The current speed or process time of the machine.
     */
    int getProcessTime();

    /**
     * @return The base or normal process time of the machine.
     */
    int getBaseProcessTime();

    /**
     * @return The current energy usage in RF per tick (can be less than 1).
     */
    double getEnergyUsage();
}
