package sonar.core.handlers.inventories;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.api.inventories.ISonarLargeInventory;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.IInventoryWrapper;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.IInsertFilter;
import sonar.core.handlers.inventories.handling.filters.SlotHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarLargeInventory implements ISonarLargeInventory {

    private NonNullList<IItemHandlerModifiable> sidedHandlers = SonarInventorySideWrapper.initWrappers(this);
    private Map<IInsertFilter, EnumFilterType> insertFilters = new HashMap<>();
    private Map<IExtractFilter, EnumFilterType> extractFilters = new HashMap<>();
    public boolean defaultExternalInsertResult = false;
    public boolean defaultExternalExtractResult = false;
    public boolean defaultInternalInsertResult = true;
    public boolean defaultInternalExtractResult = true;

    public NonNullList<InventoryLargeSlot> slots;
    public long stackSize;

    protected IInventory wrappedInv = null;
    private int[] defaultSlots = null;

    public SonarLargeInventory() {
        this(1, 64);
    }

    public SonarLargeInventory(int size, long stackSize) {
        this.stackSize = stackSize;
        setSize(size);
    }

    @Override
    public IInventory getWrapperInventory() {
        return wrappedInv == null ? wrappedInv = new IInventoryWrapper(this) : wrappedInv;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(Direction side) {
        return SonarInventorySideWrapper.getHandlerForSide(sidedHandlers, side);
    }

    @Override
    public int[] getDefaultSlots() {
        if (defaultSlots == null) {
            defaultSlots = new int[getSlots()];
            for (int i = 0; i < defaultSlots.length; i++) {
                defaultSlots[i] = i;
            }
        }
        return defaultSlots;
    }

    public void setSize(int size) {
        this.slots = NonNullList.create();
        for (int i = 0; i < size; i++) {
            slots.add(new InventoryLargeSlot(this, i));
        }
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return slots.get(slot).getAccessStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        validateSlotIndex(slot);
        if (checkInsert(slot, stack, null, EnumFilterType.INTERNAL)) {
            return slots.get(slot).insertItem(stack, simulate);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlotIndex(slot);
        if (checkExtract(slot, amount, null, EnumFilterType.INTERNAL)) {
            return slots.get(slot).extractItem(amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Map<IInsertFilter, EnumFilterType> getInsertFilters() {
        return insertFilters;
    }

    @Override
    public Map<IExtractFilter, EnumFilterType> getExtractFilters() {
        return extractFilters;
    }

    @Override
    public boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable Direction face, EnumFilterType internal) {
        boolean insert = internal.matches(EnumFilterType.INTERNAL) ? defaultInternalInsertResult : defaultExternalInsertResult;
        return SlotHelper.checkInsert(slot, stack, face, internal, this, insert) && slots.get(slot).checkInsert(stack, face, internal);
    }

    @Override
    public boolean checkExtract(int slot, int count, @Nullable Direction face, EnumFilterType internal) {
        boolean extract = internal.matches(EnumFilterType.INTERNAL) ? defaultInternalExtractResult : defaultExternalExtractResult;
        return SlotHelper.checkExtract(slot, count, face, internal, this, extract) && slots.get(slot).checkExtract(count, face, internal);
    }

    @Override
    public boolean checkDrop(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> toDrop = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            InventoryLargeSlot slot = slots.get(i);
            if (!slot.getStoredStack().isEmpty() && checkDrop(i, slot.getStoredStack())) {
                toDrop.addAll(slot.getDrops());
            }
        }
        return toDrop;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        slots.get(slot).setStackInSlot(stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) Math.min(64, stackSize);
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= slots.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");
        }
    }

    @Override
    public void readData(CompoundNBT nbt, NBTHelper.SyncType type) {
        if (type.isType(NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC)) {
            if (nbt.contains(getTagName())) { // legacy support
                ListNBT list = nbt.getList(getTagName(), 10);
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT compound = list.getCompound(i);
                    int b = compound.getInt("Slot");
                    if (b >= 0 && b < slots.size()) {
                        slots.get(b).readData(compound, type);
                    }
                }
            }
        }
    }

    @Override
    public CompoundNBT writeData(CompoundNBT nbt, NBTHelper.SyncType type) {
        if (type.isType(NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC)) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < slots.size(); i++) {
                CompoundNBT compound = new CompoundNBT();
                slots.get(i).writeData(compound, type);
                if (!compound.isEmpty()) {
                    compound.putInt("Slot", i);
                    list.add(compound);
                }
            }
            if (!list.isEmpty()) {
                nbt.put(getTagName(), list);
            }
        }
        return nbt;
    }

    protected void onContentsChanged(int slot) {
        markChanged();
    }

    //// SYNCING \\\\

    protected ISyncableListener listener;

    @Override
    public IDirtyPart setListener(ISyncableListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public ISyncableListener getListener() {
        return listener;
    }

    public void markChanged() {
        if (listener != null)
            listener.markChanged(this);
    }

    @Override
    public String getTagName() {
        return "Items";
    }

    @Override
    public boolean canSync(NBTHelper.SyncType sync) {
        return sync.isType(getSyncTypes());
    }

    public NBTHelper.SyncType[] getSyncTypes() {
        return new NBTHelper.SyncType[] { NBTHelper.SyncType.SAVE, NBTHelper.SyncType.DEFAULT_SYNC };
    }

    @Override
    public long getStackSize(int slot) {
        return slots.get(slot).getActualStored();
    }

    @Override
    public StoredItemStack getStoredStack(int slot) {
        return slots.get(slot).getLargeStack();
    }

    public static class InventoryLargeSlot implements INBTSyncable {

        public final SonarLargeInventory inventory;
        public final int slot;

        public InventoryLargeSlot(SonarLargeInventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
        }

        private ItemStack storedStack = ItemStack.EMPTY;
        private ItemStack accessStack = ItemStack.EMPTY;
        // The active size without the access stack size
        private long activeSize = 0;

        private void updateState() {
            if (storedStack.isEmpty() || getActualStored() <= 0) {
                storedStack = ItemStack.EMPTY;
                accessStack = ItemStack.EMPTY;
                activeSize = 0;
            } else {
                activeSize += accessStack.getCount();
                accessStack = ItemStack.EMPTY;
                accessStack = createAccessStack();
                activeSize -= accessStack.getCount();
            }
            inventory.markChanged();
        }

        private ItemStack createAccessStack() {
            long toFill = inventory.stackSize - getActualStored();
            if (toFill < storedStack.getMaxStackSize()) {
                ItemStack access = storedStack.copy();
                access.setCount(Math.min(storedStack.getMaxStackSize(), inventory.getSlotLimit(slot)) - (int) toFill);
                return access;
            }
            return ItemStack.EMPTY;
        }

        public long getActualStored() {
            return activeSize + accessStack.getCount();
        }

        public StoredItemStack getLargeStack() {
            return new StoredItemStack(storedStack, getActualStored());
        }

        public ItemStack getStoredStack() {
            return storedStack;
        }

        public ItemStack getAccessStack() {
            return accessStack;
        }

        public List<ItemStack> getDrops() {
            List<ItemStack> drops = new ArrayList<>();
            long drop = getActualStored();
            while (drop > 0) {
                int change = (int) Math.min(storedStack.getMaxStackSize(), drop);
                drops.add(ItemHandlerHelper.copyStackWithSize(storedStack, change));
                drop -= change;
            }
            return drops;
        }

        public void setStackInSlot(@Nonnull ItemStack stack) {
            if (this.storedStack.isEmpty() && !stack.isEmpty()) {
                this.storedStack = stack.copy();
            }
            accessStack = stack;
            updateState();
            inventory.onContentsChanged(slot);
        }

        @Override
        public void readData(CompoundNBT nbt, NBTHelper.SyncType type) {
            storedStack = new ItemStack(nbt);
            activeSize = nbt.getLong("stored");
            accessStack = ItemStack.EMPTY;
            updateState();
        }

        @Override
        public CompoundNBT writeData(CompoundNBT nbt, NBTHelper.SyncType type) {
            storedStack.writeToNBT(nbt);
            nbt.putLong("stored", getActualStored());
            return nbt;
        }

        private void doExtract(int extract) {
            activeSize -= extract;
            updateState();
            inventory.onContentsChanged(slot);
        }

        private void doInsert(int insert) {
            activeSize += insert;
            updateState();
            inventory.onContentsChanged(slot);
        }

        @Nonnull
        public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || (!storedStack.isEmpty() && !StoredItemStack.isEqualStack(stack, storedStack))) {
                return stack;
            }
            int canInsert = Math.min(stack.getCount(), (int) Math.min(Integer.MAX_VALUE, inventory.stackSize - getActualStored()));
            if (canInsert != 0) {
                if (!simulate) {
                    if (storedStack.isEmpty()) {
                        storedStack = stack.copy();
                    }
                    doInsert(canInsert);
                }
                return ItemHandlerHelper.copyStackWithSize(storedStack, stack.getCount() - canInsert);
            }
            return stack;
        }

        @Nonnull
        public ItemStack extractItem(int amount, boolean simulate) {
            if (getActualStored() <= 0 || amount <= 0 || storedStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int canExtract = (int) Math.min(Math.min(amount, storedStack.getMaxStackSize()), getActualStored());
            ItemStack extracted = ItemHandlerHelper.copyStackWithSize(storedStack, canExtract);
            if (!simulate) {
                doExtract(canExtract);
            }
            return extracted;
        }

        public boolean checkInsert(@Nonnull ItemStack stack, @Nullable Direction face, EnumFilterType internal) {
            return storedStack.isEmpty() || StoredItemStack.isEqualStack(stack, storedStack) && inventory.stackSize > getActualStored();
        }

        public boolean checkExtract(int count, @Nullable Direction face, EnumFilterType internal) {
            return getActualStored() > 0;
        }
    }
}
