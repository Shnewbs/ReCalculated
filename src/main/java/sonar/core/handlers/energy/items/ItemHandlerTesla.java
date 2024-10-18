package sonar.core.handlers.energy.items;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.world.item.ItemStack;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.asm.ItemEnergyHandler;

@ItemEnergyHandler(modid = "tesla", priority = 2)
public class ItemHandlerTesla implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.TESLA;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER).isPresent();
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER).isPresent();
    }

    @Override
    public boolean canReadEnergy(ItemStack stack) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER).isPresent();
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER)
                .map(consumer -> consumer.givePower(add, actionType.shouldSimulate()))
                .orElse(0L);
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER)
                .map(producer -> producer.takePower(remove, actionType.shouldSimulate()))
                .orElse(0L);
    }

    @Override
    public long getStored(ItemStack stack) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER)
                .map(ITeslaHolder::getStoredPower)
                .orElse(0L);
    }

    @Override
    public long getCapacity(ItemStack stack) {
        return stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER)
                .map(ITeslaHolder::getCapacity)
                .orElse(0L);
    }
}
