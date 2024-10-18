package sonar.core.network.sync;

public interface ICheckableSyncPart<T extends ISyncPart> extends ISyncPart {

    boolean isEqualPart(T part);
}
