package sonar.core.network.sync;

public interface IDirtyPart {

    ISyncableListener getListener();

    IDirtyPart setListener(ISyncableListener listener);

    // Optional methods can be uncommented if needed
    // boolean hasChanged();
    // void setChanged(boolean set);
}
