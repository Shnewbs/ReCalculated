package sonar.core.handlers.inventories;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.IInventoryWrapper;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.IInsertFilter;
import sonar.core.handlers.inventories.handling.filters.SlotHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.ISyncableListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarInventory extends ItemStackHandler implements ISonarInventory, ISyncPart {

    private NonNullList<IItemHandlerModifiable> sidedHandlers = SonarInventorySideWrapper.initWrappers(this);
    private Map<IInsertFilter, EnumFilterType> insertFilters = new HashMap<>();
    private Map<IExtractFilter, EnumFilterType> extractFilters = new HashMap<>();
    public boolean defaultExternalInsertResult = false;
    public boolean defaultExternalExtractResult = false;
    public boolean defaultInternalInsertResult = true;
    public boolean defaultInternalExtractResult = true;

    protected IInventory wrappedInv = null;
    private int[] defaultSlots = null;
    public int slotLimit = 64;

    public SonarInventory(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
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

    public List<ItemStack> slots() {
        return stacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slotLimit;
    }

    @Override
    public Map<IInsertFilter, EnumFilterType> getInsertFilters() {
        return insertFilters;
    }

    @Override
    public Map<IExtractFilter, EnumFilterType> getExtractFilters() {
        return extractFilters;
    }

    //// READ / WRITE \\\\

    @Override
    public void readData(CompoundNBT nbt, NBTHelper.SyncType type) {
        if (canSync(type)) {
            this.stacks = NonNullList.withSize(this.getSlots(), ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(nbt, this.stacks);
        }
    }

    @Override
    public CompoundNBT writeData(CompoundNBT nbt, NBTHelper.SyncType type) {
        if (canSync(type)) {
            ItemStackHelper.saveAllItems(nbt, this.stacks);
        }
        return nbt;
    }

    @Override
    public boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable Direction face, EnumFilterType internal) {
        boolean insert = internal.matches(EnumFilterType.INTERNAL) ? defaultInternalInsertResult : defaultExternalInsertResult;
        return SlotHelper.checkInsert(slot, stack, face, internal, this, insert);
    }

    @Override
    public boolean checkExtract(int slot, int count, @Nullable Direction face, EnumFilterType internal) {
        boolean extract = internal.matches(EnumFilterType.INTERNAL) ? defaultInternalExtractResult : defaultExternalExtractResult;
        return SlotHelper.checkExtract(slot, count, face, internal, this, extract);
    }

    @Override
    public boolean checkDrop(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops() {
        List<ItemStack> toDrop = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack drop = stacks.get(i);
            if (!drop.isEmpty() && checkDrop(i, drop)) {
                toDrop.add(drop);
            }
        }
        return toDrop;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (checkInsert(slot, stack, null, EnumFilterType.INTERNAL)) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (checkExtract(slot, amount, null, EnumFilterType.INTERNAL)) {
            return super.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
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
        if (listener != null) {
            listener.markChanged(this);
        }
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
        return new NBTHelper.SyncType[] { NBTHelper.SyncType.SAVE };
    }
}
