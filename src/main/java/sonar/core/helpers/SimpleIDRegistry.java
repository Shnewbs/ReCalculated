package sonar.core.helpers;

import sonar.core.api.IRegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SimpleIDRegistry<T extends IRegistryObject> extends SimpleRegistry<Integer, String> {

    // List to hold registered objects
    private final List<T> objects = new ArrayList<>();

    // Method to register an object
    public void register(T object) {
        objects.add(object);
        register(objects.size(), object.getName());
    }

    /**
     * Returns the list of registered objects.
     * Don't modify the returned list directly.
     */
    public List<T> getObjects() {
        return objects;
    }

    @Nullable
    // Method to retrieve an object by its name
    public T getObject(String name) {
        Integer id = getKey(name);
        if (id != null) {
            return objects.get(id - 1); // ID is 1-based index
        }
        return null; // Return null if the object is not found
    }

    @Nullable
    // Method to retrieve an object by its ID
    public T getObject(int id) {
        if (id > 0 && id <= objects.size()) { // Ensure ID is valid
            return objects.get(id - 1); // ID is 1-based index
        }
        return null; // Return null if the ID is invalid
    }
}
