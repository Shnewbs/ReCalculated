package sonar.core.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SonarItemSimpleFuel extends Item {

    public final int burnTime;

    public SonarItemSimpleFuel(int burnTime) {
        this.burnTime = burnTime;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return burnTime;
    }
}
