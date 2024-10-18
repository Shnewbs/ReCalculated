package sonar.core.network.sync;

import com.google.common.collect.Lists;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.List;

public abstract class SyncPart extends DirtyPart implements ISyncPart {
	public byte id = -1;
	private String name;
	private List<SyncType> types = Lists.newArrayList(SyncType.SAVE, SyncType.DEFAULT_SYNC);

	public SyncPart(int id) {
		super();
		this.id = (byte) id;
	}

	public SyncPart(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getTagName() {
		return name != null ? name : String.valueOf(id);
	}

	@Override
	public boolean canSync(SyncType syncType) {
		return types.stream().anyMatch(syncType::isType);
	}

	public SyncPart addSyncType(SyncType... add) {
		for (SyncType type : add) {
			if (!types.contains(type)) {
				types.add(type);
			}
		}
		return this;
	}

	public SyncPart removeSyncType(SyncType... remove) {
		for (SyncType type : remove) {
			types.remove(type);
		}
		return this;
	}
}
