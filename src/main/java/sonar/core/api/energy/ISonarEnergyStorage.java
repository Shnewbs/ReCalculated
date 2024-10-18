package sonar.core.api.energy;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nullable;

/**
 * Interface for managing energy storage and transfer.
 */
public interface ISonarEnergyStorage {

    /**
     * Adds energy to the storage.
     *
     * @param maxReceive The maximum amount of energy to receive.
     * @param face The side of the block (or null for internal).
     * @param action The action type (simulate or perform).
     * @return The amount of energy actually received.
     */
    long addEnergy(long maxReceive, @Nullable EnumFacing face, ActionType action);

    /**
     * Removes energy from the storage.
     *
     * @param maxExtract The maximum amount of energy to extract.
     * @param face The side of the block (or null for internal).
     * @param action The action type (simulate or perform).
     * @return The amount of energy actually extracted.
     */
    long removeEnergy(long maxExtract, @Nullable EnumFacing face, ActionType action);

    /**
     * @return The current energy level of the storage.
     */
    long getEnergyLevel();

    /**
     * @return The maximum energy capacity of the storage.
     */
    long getFullCapacity();
}
