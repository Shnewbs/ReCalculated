package sonar.core.handlers.inventories.handling.methods;

import net.minecraftforge.items.IItemHandler;
import sonar.core.handlers.inventories.handling.ITransferMethod;
import sonar.core.handlers.inventories.handling.ItemTransferHandler;

import javax.annotation.Nullable;
import java.util.Iterator;

public class TransferMethodRoundRobin implements ITransferMethod {

    public final ItemTransferHandler handler;

    //// SOURCES \\\\
    private IItemHandler currentSource;
    private int currentSourceSlot = 0;
    public Iterator<IItemHandler> sourceIterator;
    public boolean sourcesChanged = true;

    //// DESTINATIONS \\\\
    private IItemHandler currentDestination;
    private int currentDestinationSlot = 0;
    public Iterator<IItemHandler> destinationIterator;
    public boolean destinationsChanged = true;

    public TransferMethodRoundRobin(ItemTransferHandler handler) {
        this.handler = handler;
    }

    @Override
    public void transfer() {
        if (handler.sources.isEmpty() || handler.destinations.isEmpty()) {
            return;
        }

        if (sourcesChanged) {
            sourceIterator = getSourceIterator();
            if (sourceIterator == null || currentSource == null || !handler.sources.contains(currentSource)) {
                currentSourceSlot = 0;
            } else {
                sourceIterator = null;
            }
        }

        if (destinationsChanged) {
            destinationIterator = getDestinationIterator();
        }

        // Perform the transfer logic
        if (sourceIterator != null && destinationIterator != null) {
            if (currentSource == null && sourceIterator.hasNext()) {
                currentSource = sourceIterator.next();
            }

            if (currentDestination == null && destinationIterator.hasNext()) {
                currentDestination = destinationIterator.next();
            }

            // Implement transfer logic here based on the current source and destination
            // Example: transferItems(currentSource, currentSourceSlot, currentDestination, currentDestinationSlot);

            incrementSourceSlot();
            incrementDestinationSlot();
        }
    }

    @Nullable
    private IItemHandler getCurrentSource() {
        if (currentSource == null && sourceIterator != null && sourceIterator.hasNext()) {
            currentSource = sourceIterator.next();
        }
        return currentSource;
    }

    @Nullable
    private Iterator<IItemHandler> getSourceIterator() {
        if (sourceIterator == null) {
            sourceIterator = ItemTransferHandler.getItemHandlerIterator(handler.sources);
        }
        return sourceIterator;
    }

    @Nullable
    private Iterator<IItemHandler> getDestinationIterator() {
        if (destinationIterator == null) {
            destinationIterator = ItemTransferHandler.getItemHandlerIterator(handler.destinations);
        }
        return destinationIterator;
    }

    private void incrementSourceSlot() {
        currentSourceSlot++;
        if (currentSourceSlot >= currentSource.getSlots()) {
            currentSourceSlot = 0;
            sourcesChanged = true; // Reinitialize source when all slots are used
        }
    }

    private void incrementDestinationSlot() {
        currentDestinationSlot++;
        if (currentDestinationSlot >= currentDestination.getSlots()) {
            currentDestinationSlot = 0;
            destinationsChanged = true; // Reinitialize destination when all slots are used
        }
    }

    @Override
    public void onSourcesChanged() {
        sourcesChanged = true;
    }

    @Override
    public void onDestinationsChanged() {
        destinationsChanged = true;
    }
}
