package sonar.core.network.sync;

public class DirtyPart implements IDirtyPart {

	protected ISyncableListener listener;

	public DirtyPart() {
	}

	@Override
	public DirtyPart setListener(ISyncableListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public ISyncableListener getListener() {
		return listener;
	}

	public void markDirty() { // Updated method name to follow NeoForge conventions
		if (listener != null) {
			listener.markChanged(this);
		}
	}
}
