package sonar.core.network.sync;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import net.minecraft.util.Direction; // Updated for Direction
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.core.integration.SonarLoader;

import javax.annotation.Nonnull;

public class SyncItemEnergyStorage extends SyncEnergyStorage implements ICapabilityProvider {

	public ItemStack stack;

	public SyncItemEnergyStorage(ItemStack stack, int capacity) {
		super(capacity);
		this.stack = stack;
	}

	public SyncItemEnergyStorage(ItemStack stack, int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
		this.stack = stack;
	}

	public SyncItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
		this.stack = stack;
	}

	public SyncItemEnergyStorage setItemStack(ItemStack stack) {
		if (!stack.isEmpty()) {
			this.stack = stack;
			if (stack.hasTag()) { // Updated to use hasTag
				readFromNBT(stack.getTag()); // Updated to use getTag
			}
		}
		return this;
	}

	@Override
	public void markChanged() {
		super.markChanged();
		if (!stack.isEmpty()) {
			if (!stack.hasTag()) { // Updated to use hasTag
				stack.setTag(new CompoundNBT()); // Updated to use CompoundNBT
			}
			stack.setTag(writeToNBT(stack.getTag())); // Updated to use setTag
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, Direction facing) { // Updated to use Direction
		if (capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		if (SonarLoader.teslaLoaded) {
			return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER;
		}
		return false;
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, Direction facing) { // Updated to use Direction
		if (capability == CapabilityEnergy.ENERGY) {
			return (T) this.getInternalWrapper();
		}
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER) {
				return (T) this.getInternalWrapper();
			}
		}
		return null;
	}
}
