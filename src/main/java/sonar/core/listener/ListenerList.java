package sonar.core.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.ListHelper;
import sonar.core.utils.Pair;

public class ListenerList<L extends ISonarListener> {

    public final int maxTypes;
    public boolean isValid;
    public boolean hasListeners;
    public ArrayList<ISonarListenable<L>> subLists = new ArrayList<>();
    public ArrayList<ISonarListenable<L>> masterLists = new ArrayList<>();
    public ArrayList<ListenerTally<L>> listenerTallies = new ArrayList<>();
    public boolean[] hasTypes;

    public ListenerList(int maxTypes) {
        this.maxTypes = maxTypes;
        this.isValid = true;
        this.hasTypes = new boolean[maxTypes];
    }

    public void updateState() {
        boolean[] states = new boolean[maxTypes];
        lists: for (ListenerList<L> list : this.getValidLists(false)) {
            for (ListenerTally<L> tally : list.listenerTallies) {
                boolean hasAll = true;
                for (int i = 0; i < states.length; i++) {
                    if (!states[i]) {
                        if (tally.getTally(i) > 0)
                            states[i] = true;
                        else
                            hasAll = false;
                    }
                }
                if (hasAll) {
                    break lists;
                }
            }
        }
        hasTypes = states;
        hasListeners = checkForListeners();
        masterLists.forEach(l -> l.getListenerList().updateState());
    }

    public boolean hasListeners(int type) {
        return this.hasTypes[type];
    }

    public boolean hasListeners() {
        return hasListeners;
    }

    public boolean hasListeners(List<Integer> types) {
        for (int i : types) {
            if (hasTypes[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean hasListeners(int... types) {
        for (int i : types) {
            if (hasTypes[i]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForListeners() {
        for (boolean bool : hasTypes) {
            if (bool) {
                return true;
            }
        }
        return false;
    }

    public void addSubListenable(ISonarListenable<L> sub) {
        if (!subLists.contains(sub) && sub.isValid()) {
            subLists.add(sub);
            onSubListenableAdded(sub);
            updateState();
        }
    }

    public void addMasterListenable(ISonarListenable<L> master) {
        if (!masterLists.contains(master) && master.isValid()) {
            masterLists.add(master);
        }
    }

    public void removeSubListenable(ISonarListenable<L> sub) {
        if (subLists.remove(sub)) {
            onSubListenableRemoved(sub);
            updateState();
        }
    }

    public void removeMasterListenable(ISonarListenable<L> master) {
        masterLists.remove(master);
    }

    public void clearSubLists(boolean notify) {
        if (notify) {
            for (ListenerTally<L> tally : listenerTallies) {
                onListenerRemoved(tally);
            }
            clearSubLists(false);
        } else {
            subLists.clear();
            masterLists.clear();
            updateState();
        }
    }

    public void validateList() {
        isValid = true;
    }

    public void invalidateList() {
        clearSubLists(true);
        listenerTallies.clear();
        subLists.clear();
        masterLists.clear();
        isValid = false;
        hasTypes = new boolean[maxTypes];
    }

    private List<ListenerList<L>> getValidLists(boolean local) {
        List<ListenerList<L>> getValidLists = new ArrayList<>();
        getValidLists.add(this);
        if (!local) {
            for (ISonarListenable<L> listenable : new ArrayList<>(subLists)) {
                if (listenable.isValid()) {
                    getValidLists.add(listenable.getListenerList());
                } else {
                    subLists.remove(listenable);
                }
            }
        }
        return getValidLists;
    }

    public void addListener(EntityPlayer listener, Enum... enums) {
        addListener((L) findListener(listener), ListHelper.getOrdinals(enums));
    }

    public void addListener(EntityPlayer listener, int... types) {
        addListener((L) findListener(listener), types);
    }

    public void addListener(L listener, Enum... enums) {
        addListener(listener, ListHelper.getOrdinals(enums));
    }

    public void addListener(L listener, int... types) {
        if (listener.isValid()) {
            Pair<ListenerTally<L>, Boolean> tally = getTally(listener, true);
            if (tally.a != null) {
                for (int t : types) {
                    tally.a.tallies[t]++;
                }
                if (tally.b) {
                    onListenerAdded(tally.a);
                }
                updateState();
            }
        }
    }

    public void removeListener(EntityPlayer listener, boolean local, Enum... enums) {
        removeListener((L) findListener(listener), local, ListHelper.getOrdinals(enums));
    }

    public void removeListener(EntityPlayer listener, boolean local, int... types) {
        removeListener((L) findListener(listener), local, types);
    }

    public void removeListener(ListenerTally<L> listener, boolean local, Enum... enums) {
        removeListener(listener.listener, local, ListHelper.getOrdinals(enums));
    }

    public void removeListener(L listener, boolean local, Enum... enums) {
        removeListener(listener, local, ListHelper.getOrdinals(enums));
    }

    public void removeListener(L listener, boolean local, int... types) {
        List<Integer> valid = getValidTypes(types);
        for (ListenerList<L> list : getValidLists(false)) {
            Pair<ListenerTally<L>, Boolean> tally = list.getTally(listener, false);
            if (tally.a != null) {
                for (int t : Lists.newArrayList(valid)) {
                    if (tally.a.tallies[t] > 0) {
                        tally.a.tallies[t]--;
                        valid.remove(Integer.valueOf(t));
                    }
                }
                if (wasRemoved(tally.a)) {
                    list.onListenerRemoved(tally.a);
                    list.updateState();
                }
            }
        }
    }

    public void clearListener(L listener) {
        Pair<ListenerTally<L>, Boolean> tally = getTally(listener, false);
        if (tally.a != null && listenerTallies.remove(tally.a)) {
            onListenerRemoved(tally.a);
            updateState();
        }
    }

    public PlayerListener findListener(EntityPlayer player) {
        for (ListenerList<L> list : getValidLists(false)) {
            for (ListenerTally<L> tally : list.listenerTallies) {
                if (tally.listener instanceof PlayerListener && ((PlayerListener) tally.listener).player.isEntityEqual(player)) {
                    return (PlayerListener) tally.listener;
                }
            }
        }
        return new PlayerListener(player);
    }

    private Pair<ListenerTally<L>, Boolean> getTally(L listener, boolean create) {
        Iterator<ListenerTally<L>> i = listenerTallies.iterator();
        while (i.hasNext()) {
            ListenerTally<L> tally = i.next();
            if (!tally.isValid()) {
                i.remove();
            } else if (listener.hashCode() == tally.listener.hashCode() || listener.equals(tally.listener)) {
                return new Pair<>(tally, false);
            }
        }
        if (create) {
            ListenerTally<L> created = new ListenerTally<>(this, listener, maxTypes);
            listenerTallies.add(created);
            return new Pair<>(created, true);
        }
        return new Pair<>(null, false);
    }

    public List<ListenerTally<L>> getTallies(Enum... enums) {
        return getTallies(ListHelper.getOrdinals(enums));
    }

    public List<ListenerTally<L>> getTallies(int... types) {
        List<Integer> valid = getValidTypes(types);
        List<ListenerTally<L>> tallies = new ArrayList<>();
        if (valid.isEmpty() || !hasListeners(valid)) {
            return tallies;
        }
        getValidLists(false).forEach(list -> list.listenerTallies.forEach(tally -> {
            if (!tallies.contains(tally)) {
                for (int type : valid) {
                    if (tally.getTally(type) > 0) {
                        tallies.add(tally);
                    }
                }
            }
        }));
        return tallies;
    }

    public List<L> getListeners(Enum... enums) {
        return getListeners(ListHelper.getOrdinals(enums));
    }

    /**
     * Gets all listeners, including those in sublists.
     */
    public List<L> getListeners(int... types) {
        List<Integer> valid = getValidTypes(types);
        List<L> listeners = new ArrayList<>();
        if (valid.isEmpty() || !hasListeners(valid)) {
            return listeners;
        }
        getValidLists(false).forEach(list -> list.listenerTallies.forEach(tally -> {
            if (!listeners.contains(tally.listener)) {
                for (int type : valid) {
                    if (tally.getTally(type) > 0) {
                        listeners.add(tally.listener);
                    }
                }
            } else {
                // FIXME combine tallies?
            }
        }));
        return listeners;
    }

    private List<Integer> getValidTypes(int[] types) {
        List<Integer> valid = new ArrayList<>();
        for (int t : types) {
            if (hasListeners(t)) {
                valid.add(t);
            }
        }
        return valid;
    }

    /**
     * Returns true if the tally was removed.
     */
    private boolean wasRemoved(ListenerTally<L> tally) {
        return !tally.isValid() && listenerTallies.remove(tally);
    }

    public void onListenerAdded(ListenerTally<L> tally) {}

    public void onListenerRemoved(ListenerTally<L> tally) {}

    public void onSubListenableAdded(ISonarListenable<L> tally) {}

    public void onSubListenableRemoved(ISonarListenable<L> tally) {}

}
