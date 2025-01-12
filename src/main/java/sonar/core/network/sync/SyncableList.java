package sonar.core.network.sync;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.nbt.CompoundNBT; // Updated to use CompoundNBT
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncableList {

	private ArrayList<ISyncPart> syncParts = new ArrayList<>();
	private ArrayList<ISyncPart> changedSyncParts = new ArrayList<>();
	private ArrayList<IDirtyPart> dirtyParts = new ArrayList<>();
	private ArrayList<IDirtyPart> changedDirtyParts = new ArrayList<>();

	public ISyncableListener listener;

	public SyncableList(ISyncableListener listener) {
		this.listener = listener;
	}

	public void addPart(IDirtyPart part) {
		ArrayList list = part instanceof ISyncPart ? syncParts : dirtyParts;
		ArrayList changeList = part instanceof ISyncPart ? changedSyncParts : changedDirtyParts;
		if (!list.contains(part)) {
			list.add(part);
			changeList.add(part);
			part.setListener(listener);
		}
	}

	public void removePart(IDirtyPart part) {
		ArrayList list = part instanceof ISyncPart ? syncParts : dirtyParts;
		list.remove(part);
	}

	public void addParts(Collection<? extends IDirtyPart> parts) {
		parts.forEach(this::addPart);
	}

	public void addParts(IDirtyPart... parts) {
		for (IDirtyPart part : parts) {
			addPart(part);
		}
	}

	public void markSyncPartChanged(IDirtyPart part) {
		ArrayList list = part instanceof ISyncPart ? changedSyncParts : changedDirtyParts;
		list.add(part);
	}

	public void onPartSynced(IDirtyPart part) {
		// ArrayList list = (part instanceof ISyncPart ? changedSyncParts : changedDirtyParts);
		// list.remove(part);
	}

	public void onPartsSynced() {
		changedSyncParts.clear();
		changedDirtyParts.clear();
	}

	public ArrayList<ISyncPart> getSyncList(SyncType type) {
		return type.mustSync() ? syncParts : changedSyncParts;
	}

	public ArrayList<ISyncPart> getStandardSyncParts() {
		return syncParts;
	}

	/**
	 * ONLY WORKS WITH LISTS WITH ISYNCPARTS in EXACTLY THE SAME POSITIONS!!!!
	 */
	public void copyFrom(SyncableList list) {
		CompoundNBT copy = new CompoundNBT(); // Updated to use CompoundNBT
		NBTHelper.writeSyncParts(copy, SyncType.SAVE, list, true);
		NBTHelper.readSyncParts(copy, SyncType.SAVE, this);
	}
}
