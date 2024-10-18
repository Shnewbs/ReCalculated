package sonar.core.handlers.energy.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.asm.ItemEnergyHandler;

@ItemEnergyHandler(modid = "sonarcore", priority = 0)
public class ItemHandlerForge implements IItemEnergyHandler {

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.FE;
    }

    @Override
    public boolean canAddEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::canReceive).orElse(false);
    }

    @Override
    public boolean canRemoveEnergy(ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::canExtract).orElse(false);
    }

    @Override
    public boolean canReadEnergy(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Override
    public long addEnergy(long add, ItemStack stack, ActionType actionType) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(storage -> (long) storage.receiveEnergy((int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate())).orElse(0L);
    }

    @Override
    public long removeEnergy(long remove, ItemStack stack, ActionType actionType) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(storage -> (long) storage.extractEnergy((int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate())).orElse(0L);
    }

    @Override
    public long getStored(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public long getCapacity(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }
}
