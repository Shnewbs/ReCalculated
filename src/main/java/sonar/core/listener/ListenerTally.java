package sonar.core.listener;

import sonar.core.helpers.ListHelper;

public class ListenerTally<T extends ISonarListener> {

	public T listener;
	public int[] tallies;
	public ListenerList<T> source;

	public ListenerTally(ListenerList<T> source, T listener, int types) {
		this.source = source;
		this.listener = listener;
		this.tallies = new int[types];
	}

	public int getTally(int type) {
		return tallies[type];
	}

	public int getTally(Enum<?> type) {
		return getTally(type.ordinal());
	}

	public boolean hasTally(Enum<?> type) {
		return getTally(type) > 0;
	}

	public boolean hasTally(int type) {
		return getTally(type) > 0;
	}

	public boolean isValid() {
		return listener != null && listener.isValid() && hasTallies();
	}

	public boolean hasTallies() {
		for (int i : tallies) {
			if (i > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return listener.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ListenerTally && listener.equals(((ListenerTally<?>) obj).listener);
	}

	/** Make sure you update the list's state! */
	public void addTallies(int amount, Enum<?>... enums) {
		addTallies(amount, ListHelper.getOrdinals(enums));
	}

	/** Make sure you update the list's state! */
	public void addTallies(int amount, int... types) {
		for (int type : types) {
			tallies[type] += amount;
		}
	}

	/** Make sure you update the list's state! */
	public void removeTallies(int amount, Enum<?>... enums) {
		removeTallies(amount, ListHelper.getOrdinals(enums));
	}

	/** Make sure you update the list's state! */
	public void removeTallies(int amount, int... types) {
		for (int type : types) {
			tallies[type] -= amount;
		}
	}
}
