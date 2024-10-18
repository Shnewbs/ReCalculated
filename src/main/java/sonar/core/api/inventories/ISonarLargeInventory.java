package sonar.core.api.inventories;

public interface ISonarLargeInventory extends ISonarInventory {

    /**
     * Gets the stack size for a specific slot.
     *
     * @param slot The slot to check.
     * @return The stack size of the item in the slot.
     */
    long getStackSize(int slot);

    /**
     * Gets the stored item stack for a specific slot.
     *
     * @param slot The slot to check.
     * @return The stored item stack in the slot.
     */
    StoredItemStack getStoredStack(int slot);
}
