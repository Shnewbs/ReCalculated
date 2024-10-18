package sonar.core.api.nbt;

import io.netty.buffer.ByteBuf;
import sonar.core.api.IRegistryObject;

/**
 * Interface for objects that can be serialized to and from a ByteBuf, as well as NBT.
 *
 * @param <T> The type of object.
 */
public interface IBufObject<T> extends IRegistryObject, INBTObject<T> {

    /**
     * Reads the object data from a ByteBuf.
     *
     * @param buf The buffer to read from.
     */
    void readFromBuf(ByteBuf buf);

    /**
     * Writes the object data to a ByteBuf.
     *
     * @param buf The buffer to write to.
     */
    void writeToBuf(ByteBuf buf);
}
