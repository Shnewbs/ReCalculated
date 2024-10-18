package sonar.core.api.inventories;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.ISonarStack;
import sonar.core.helpers.NBTHelper.SyncType;

public class StoredItemStack implements ISonarStack<StoredItemStack> {

	public ItemStack item = ItemStack.EMPTY;
	public long stored;

	public StoredItemStack() {}

	public StoredItemStack(ItemStack stack) {
		this.item = stack.copy();
		this.stored = stack.getCount();
		this.item.setCount(1);
	}

	public StoredItemStack(ItemStack stack, long stored) {
		this.item = stack.copy();
		this.stored = stored;
		this.item.setCount(1);
	}

	public void add(ItemStack stack) {
		if (equalStack(stack)) {
			stored += stack.getCount();
		}
	}

	public void remove(ItemStack stack) {
		if (equalStack(stack)) {
			stored -= stack.getCount();
		}
	}

	@Override
	public void add(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored += stack.stored;
		}
	}

	@Override
	public void remove(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored -= stack.stored;
		}
	}

	@Override
	public StoredItemStack copy() {
		return new StoredItemStack(this.item, this.stored);
	}

	public StoredItemStack setStackSize(StoredItemStack stack) {
		this.stored = stack == null ? 0 : stack.getStackSize();
		return this;
	}

	@Override
	public StoredItemStack setStackSize(long size) {
		this.stored = size;
		return this;
	}

	public static boolean isEqualStack(ItemStack main, ItemStack adding) {
		return !main.isEmpty() && !adding.isEmpty() && main.isItemEqual(adding) && ItemStack.areItemStackTagsEqual(adding, main);
	}

	public boolean equalStack(ItemStack stack) {
		return isEqualStack(item, stack);
	}

	@Override
	public void readData(CompoundNBT nbt, SyncType type) {
		item = ItemStack.of(nbt);
		stored = nbt.getLong("stored");
	}

	@Override
	public CompoundNBT writeData(CompoundNBT nbt, SyncType type) {
		item.save(nbt);
		nbt.putLong("stored", stored);
		return nbt;
	}

	public static StoredItemStack readFromBuf(ByteBuf buf) {
		return new StoredItemStack(ByteBufUtils.readItemStack(buf), buf.readLong());
	}

	public static void writeToBuf(ByteBuf buf, StoredItemStack storedStack) {
		ByteBufUtils.writeItemStack(buf, storedStack.item);
		buf.writeLong(storedStack.stored);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StoredItemStack) {
			StoredItemStack target = (StoredItemStack) obj;
			return equalStack(target.item) && this.stored == target.stored;
		}
		return false;
	}

	public ItemStack getItemStack() {
		return item;
	}

	@Override
	public long getStackSize() {
		return stored;
	}

	public int getValidStackSize() {
		return (int) Math.min(stored, item.getMaxStackSize());
	}

	public int getItemDamage() {
		return item.getDamageValue();
	}

	public CompoundNBT getTagCompound() {
		return item.getTag();
	}

	public ItemStack getFullStack() {
		int min = getValidStackSize();
		ItemStack stack = item.copy();
		stack.setCount(min);
		return stack;
	}

	public ItemStack getActualStack() {
		ItemStack fullStack = getFullStack();
		if (fullStack.getCount() <= 0) {
			return ItemStack.EMPTY;
		}
		return fullStack;
	}

	public static ItemStack getActualStack(StoredItemStack stack) {
		if (stack == null) {
			return ItemStack.EMPTY;
		}
		return stack.getActualStack();
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.ITEMS;
	}

	@Override
	public String toString() {
		if (!item.isEmpty()) {
			return this.stored + "x" + this.item.getDescriptionId() + '@' + item.getDamageValue();
		} else {
			return super.toString() + " : EMPTY";
		}
	}
}
