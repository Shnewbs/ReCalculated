package sonar.core.api;

import io.netty.buffer.ByteBuf;

/**
 * Interface for a flexible container capable of handling dynamic states.
 *
 * @param <T> The type of state the container holds.
 */
public interface IFlexibleContainer<T> {

    /**
     * Refreshes the current state of the container.
     */
    void refreshState();

    /**
     * Gets the current state of the container.
     *
     * @return The current state.
     */
    T getCurrentState();

    /**
     * Interface for a syncable flexible container, allowing state synchronization.
     */
    interface Syncable extends IFlexibleContainer {

        /**
         * Reads the state from a ByteBuf.
         *
         * @param buf The ByteBuf to read from.
         */
        void readState(ByteBuf buf);

        /**
         * Writes the state to a ByteBuf.
         *
         * @param buf The ByteBuf to write to.
         */
        void writeState(ByteBuf buf);
    }
}
