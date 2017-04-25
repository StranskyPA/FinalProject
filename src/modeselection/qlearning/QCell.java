package modeselection.qlearning;

import java.util.EnumMap;
import java.util.Map.Entry;

public class QCell<M extends Enum<M>> {
	private EnumMap<M,QActionInfo> mapping;
	private int targetVisits;
	
	public QCell(Class<M> actionClass, int targetVisits) {
		this.targetVisits = targetVisits;
		mapping = new EnumMap<>(actionClass);
		for (M action: actionClass.getEnumConstants()) {
			mapping.put(action, new QActionInfo());
		}
	}
	
	private int visitsLeft(int visits) {
		return targetVisits > visits ? targetVisits - visits : 0;
	}
	
	public double getQFor(M action) {
		return mapping.get(action).getQ();
	}
	
	public void setQFor(M action, double updatedQ) {
		mapping.get(action).setQ(updatedQ);
	}
	
	public double getMaxQ() {
		return mapping.values().stream()
				.reduce((v1, v2) -> {
					return v1.getQ() >= v2.getQ() ? v1 : v2;
				}).get().getQ();
	}
	
	public M getBest() {
		return mapping.entrySet().stream()
				.reduce((e1, e2) -> {
					int e1Left = visitsLeft(e1.getValue().getVisits());
					int e2Left = visitsLeft(e2.getValue().getVisits());
					
					if (e1Left > e2Left || e1Left == e2Left && e1.getValue().getQ() >= e2.getValue().getQ()) {
						return e1;
					} else {
						return e2;
					} 
				}).get().getKey();
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Entry<M, QActionInfo> action: mapping.entrySet()) {
			result.append(String.format("%s(%s)", action.getKey().name(), action.getValue().toString()));
		}
		return result.toString();
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
}
