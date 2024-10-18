package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import net.minecraft.util.Direction; // Updated to use Direction
import net.minecraftforge.common.util.INBTSerializable;
import sonar.core.api.energy.ISonarEnergyStorage;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.energy.InternalEnergyStorageWrapper;
import sonar.core.helpers.NBTHelper.SyncType;

import javax.annotation.Nullable;

public class SyncEnergyStorage extends DirtyPart implements ISonarEnergyStorage, INBTSerializable<CompoundNBT>, ISyncPart {

	private long energy;
	private long capacity;
	private long maxReceive;
	private long maxExtract;
	private InternalEnergyStorageWrapper[] wrappers = new InternalEnergyStorageWrapper[7];

	private String tagName = "energyStorage";

	public SyncEnergyStorage(int capacity) {
		this(capacity, capacity, capacity);
	}

	public SyncEnergyStorage(int capacity, int maxTransfer) {
		this(capacity, maxTransfer, maxTransfer);
	}

	public SyncEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public InternalEnergyStorageWrapper getOrCreateWrapper(@Nullable Direction face) {
		int id = face == null ? 6 : face.getIndex();
		if (wrappers[id] != null) {
			return wrappers[id];
		} else {
			return wrappers[id] = new InternalEnergyStorageWrapper(this, face);
		}
	}

	public InternalEnergyStorageWrapper getInternalWrapper() {
		return getOrCreateWrapper(null);
	}

	public SyncEnergyStorage setCapacity(int capacity) {
		this.capacity = capacity;

		if (energy > capacity) {
			energy = capacity;
		}
		markChanged();
		return this;
	}

	public SyncEnergyStorage setMaxTransfer(int maxTransfer) {
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public SyncEnergyStorage setMaxReceive(int maxReceive) {
		this.maxReceive = maxReceive;
		markChanged();
		return this;
	}

	public SyncEnergyStorage setMaxExtract(int maxExtract) {
		this.maxExtract = maxExtract;
		markChanged();
		return this;
	}

	public long getMaxReceive() {
		return maxReceive;
	}

	public long getMaxExtract() {
		return maxExtract;
	}

	public void setEnergyStored(long energy) {
		this.energy = energy;
		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		this.markChanged();
	}

	public void modifyEnergyStored(long energy) {
		this.energy += energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		this.markChanged();
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		if (energy < 0) {
			energy = 0;
		}
		buf.writeLong(energy);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.energy = buf.readLong();

		if (energy > capacity) {
			energy = capacity;
		}
	}

	public SyncEnergyStorage readFromNBT(CompoundNBT nbt) { // Updated to use CompoundNBT
		this.energy = nbt.getLong("Energy");

		if (energy > capacity) {
			energy = capacity;
		}
		return this;
	}

	public CompoundNBT writeToNBT(CompoundNBT nbt) { // Updated to use CompoundNBT
		if (energy < 0) {
			energy = 0;
		}
		nbt.putLong("Energy", energy); // Updated to use putLong
		return nbt;
	}

	@Override
	public final CompoundNBT writeData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		CompoundNBT energyTag = new CompoundNBT(); // Updated to use CompoundNBT
		this.writeToNBT(energyTag);
		nbt.put(getTagName(), energyTag); // Updated to use put
		return nbt;
	}

	@Override
	public final void readData(CompoundNBT nbt, SyncType type) { // Updated to use CompoundNBT
		if (nbt.contains(getTagName())) { // Updated to use contains
			this.readFromNBT(nbt.getCompound(getTagName())); // Updated to use getCompound
		}
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	public SyncEnergyStorage setTagName(String tagName) {
		this.tagName = tagName;
		return this;
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	///// * SONAR *//////
	@Override
	public long addEnergy(long maxReceive, Direction face, ActionType action) { // Updated to use Direction
		long add = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!action.shouldSimulate()) {
			energy += add;
			this.markChanged();
		}
		return add;
	}

	@Override
	public long removeEnergy(long maxExtract, Direction face, ActionType action) { // Updated to use Direction
		long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!action.shouldSimulate()) {
			energy -= energyExtracted;
			this.markChanged();
		}
		return energyExtracted;
	}

	public boolean canExtract(Direction face) { // Updated to use Direction
		return true;
	}

	public boolean canReceive(Direction face) { // Updated to use Direction
		return true;
	}

	@Override
	public long getEnergyLevel() {
		return energy;
	}

	@Override
	public long getFullCapacity() {
		return capacity;
	}

	@Override
	public CompoundNBT serializeNBT() { // Updated to use CompoundNBT
		return this.writeToNBT(new CompoundNBT()); // Updated to use CompoundNBT
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) { // Updated to use CompoundNBT
		this.readData(nbt, SyncType.SAVE);
	}
}
