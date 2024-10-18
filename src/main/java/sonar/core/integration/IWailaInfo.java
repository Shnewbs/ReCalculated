package sonar.core.integration;

import net.minecraft.block.state.IBlockState;

import java.util.List;

public interface IWailaInfo {

    /**
     * Retrieve additional information for Waila (What Am I Looking At) display.
     *
     * @param currentTip The current tooltip text.
     * @param state      The block state being queried.
     * @return A list of strings representing the additional information to be displayed.
     */
    List<String> getWailaInfo(List<String> currentTip, IBlockState state);
}
