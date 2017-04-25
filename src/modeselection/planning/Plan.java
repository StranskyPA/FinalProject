package modeselection.planning;

import java.util.Optional;

public class Plan<C extends Enum<C>, M extends Enum<M>> {
	private M action;
	private C result;
	private Optional<Plan<C,M>> next;
	
	public Plan(M action, C result) {
		this(action, result, Optional.empty());
	}
	
	public Plan(M action, C result, Optional<Plan<C,M>> next) {
		this.action = action;
		this.result = result;
		this.next = next;
	}
	
	public boolean contains(C state) {
		Optional<Plan<C, M>> step = Optional.of(this);
		while (step.isPresent()) {
			if (step.get().result == state) {
				return true;
			} else {
				step = step.get().next;
			}
		}
		return false;
	}
	
	public M getAction() {return action;}
	
	public String toString() {
		StringBuilder planStr = new StringBuilder();
		Optional<Plan<C, M>> step = Optional.of(this);
		while (step.isPresent()) {
			planStr.append("[");
			planStr.append(step.get().action.name());
			planStr.append(";");
			planStr.append(step.get().result.name());
			planStr.append("]");
			step = step.get().next;
		}
		return planStr.toString();
	}
}
