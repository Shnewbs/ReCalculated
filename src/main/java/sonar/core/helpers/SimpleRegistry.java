package sonar.core.helpers;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SimpleRegistry<K, V> {

    // Maps for registry: name to item and item to name
    private final HashMap<K, V> registryNameToItem = new HashMap<>();
    private final HashMap<V, K> registryItemToName = new HashMap<>();

    // Method to register a key-value pair in the registry
    public void register(K name, V item) {
        registryNameToItem.put(name, item);
        registryItemToName.put(item, name);
        log(name, item); // Log the registration (optional, can be overridden)
    }

    @Nullable
    // Method to retrieve an item by its name
    public V getValue(K name) {
        return registryNameToItem.get(name);
    }

    @Nullable
    // Method to retrieve a name by its item
    public K getKey(V item) {
        return registryItemToName.get(item);
    }

    // Method to log the registration, can be overridden for custom logging
    protected void log(K name, V item) {
        // Default implementation does nothing
    }

    // Method to get the count of registered items
    public int getRegisterCount() {
        return registryNameToItem.size();
    }
}
