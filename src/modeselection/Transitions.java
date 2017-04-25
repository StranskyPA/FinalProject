package modeselection;

import java.util.ArrayList;

public class Transitions<C extends Enum<C>, M extends Enum<M>> {
	private ArrayList<C> flags = new ArrayList<>();
	private ArrayList<M> modes = new ArrayList<>();
	
	public Transitions() {}
	
	public Transitions(Transitions<C,M> other) {
		for (C flag: other.flags) {
			flags.add(flag);
		}
		for (M mode: other.modes) {
			modes.add(mode);
		}
	}
	
	public Transitions<C,M> add(C condition, M newMode) {
		flags.add(condition);
		modes.add(newMode);
		return this;
	}
  	
	public M getMode(M current, SensedValues<C> conditions) {
		for (int i = 0; i < modes.size(); i++) {
			 if (conditions.contains(flags.get(i))) {
				 return modes.get(i);
			 }
		}
		return current;
	}
}
