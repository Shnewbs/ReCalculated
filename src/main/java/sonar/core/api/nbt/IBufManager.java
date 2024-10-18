package sonar.core.api.nbt;

import io.netty.buffer.ByteBuf;

/**
 * Deprecated interface for managing reading and writing objects to/from a ByteBuf.
 *
 * @param <T> The type of object that implements IBufObject.
 */
@Deprecated
public interface IBufManager<T extends IBufObject> extends INBTManager<T> {

    /**
     * Reads the object from the buffer.
     *
     * @param buf The ByteBuf to read from.
     * @return The object read from the buffer.
     */
    T readFromBuf(ByteBuf buf);

    /**
     * Writes the object to the buffer.
     *
     * @param buf The ByteBuf to write to.
     * @param object The object to write.
     */
    void writeToBuf(ByteBuf buf, T object);
}
