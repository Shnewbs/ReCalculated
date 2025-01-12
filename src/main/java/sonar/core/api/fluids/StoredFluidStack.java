package sonar.core.api.fluids;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.ISonarStack;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public class StoredFluidStack implements ISonarStack<StoredFluidStack> {

	public FluidStack fluid;
	public long stored, capacity;

	public StoredFluidStack() {}

	public StoredFluidStack(FluidStack stack) {
		this.fluid = stack.copy();
		this.stored = stack.getAmount();
		this.capacity = stack.getAmount();
	}

	public StoredFluidStack(FluidStack stack, long capacity) {
		this.fluid = stack.copy();
		this.stored = stack.getAmount();
		this.capacity = capacity;
	}

	public StoredFluidStack(FluidStack stack, long stored, long capacity) {
		this.fluid = stack.copy();
		this.stored = stored;
		this.capacity = capacity;
	}

	@Override
	public void add(StoredFluidStack stack) {
		if (equalStack(stack.fluid)) {
			stored += stack.stored;
			capacity += stack.capacity;
		}
	}

	@Override
	public void remove(StoredFluidStack stack) {
		if (equalStack(stack.fluid)) {
			stored -= stack.stored;
			capacity -= stack.capacity;
		}
	}

	@Override
	public StoredFluidStack copy() {
		return new StoredFluidStack(this.fluid, this.stored, this.capacity);
	}

	public StoredFluidStack setStackSize(StoredFluidStack stack) {
		this.stored = stack == null ? 0 : stack.stored;
		return this;
	}

	@Override
	public StoredFluidStack setStackSize(long size) {
		this.stored = size;
		return this;
	}

	public static boolean isEqualStack(FluidStack main, FluidStack stack) {
		return main != null && stack != null && main.isFluidEqual(stack);
	}

	public boolean equalStack(FluidStack stack) {
		return isEqualStack(this.fluid, stack);
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) {
		this.fluid = FluidStack.loadFluidStackFromNBT(nbt);
		this.stored = nbt.getLong("stored");
		this.capacity = nbt.getLong("capacity");
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) {
		fluid.writeToNBT(nbt);
		nbt.putLong("stored", stored);
		nbt.putLong("capacity", capacity);
		return nbt;
	}

	public static StoredFluidStack readFromBuf(ByteBuf buf) {
		return new StoredFluidStack(NBTHelper.readFluidFromBuf(buf), buf.readLong(), buf.readLong());
	}

	public static void writeToBuf(ByteBuf buf, StoredFluidStack storedStack) {
		NBTHelper.writeFluidToBuf(storedStack.fluid, buf);
		buf.writeLong(storedStack.stored);
		buf.writeLong(storedStack.capacity);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StoredFluidStack) {
			StoredFluidStack target = (StoredFluidStack) obj;
			return equalStack(target.fluid) && this.stored == target.stored && this.capacity == target.capacity;
		}
		return false;
	}

	public FluidStack getFullStack() {
		FluidStack stack = fluid.copy();
		stack.setAmount((int) Math.min(stored, Integer.MAX_VALUE));
		return stack;
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.FLUIDS;
	}

	@Override
	public long getStackSize() {
		return stored;
	}

	@Override
	public String toString() {
		if (fluid != null) {
			return this.stored + " mb " + "x " + this.fluid.getTranslationKey();
		} else {
			return super.toString() + " : NULL";
		}
	}
}
