package sonar.core.handlers.inventories.handling.filters;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface IExtractFilter {

    IExtractFilter BLOCK_EXTRACT = (slot, count, side) -> false;

    /**
     * @return true = extraction is allowed! - false = extraction is blocked - null = the filter has no preference
     */
    @Nullable
    Boolean canExtract(int slot, int amount, Direction face);
}
