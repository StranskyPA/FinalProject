package modeselection.qlearning;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class QTable<C extends Enum<C>, M extends Enum<M>> {
	private HashMap<Set<C>, QCell<M>> table = new HashMap<>();
	private int targetVisits;
	private Class<M> actionClass;
	
	public QTable(Class<M> actionClass, int targetVisits) {
		this.targetVisits = targetVisits;
		this.actionClass = actionClass;
	}
	
	public QCell<M> get(Set<C> state) {
		if (!table.containsKey(state)) {
			table.put(state, new QCell<M>(actionClass, targetVisits));
		}
		return table.get(state);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Entry<Set<C>, QCell<M>> state: table.entrySet()) {
			result.append('(');
			for (C c: state.getKey()) {
				result.append(c.name());
				result.append(",");
			}
			result.deleteCharAt(result.length() - 1);
			result.append("):");
			result.append(state.getValue().toString());
			result.append('\n');
		}
		return result.toString();
	}
}
