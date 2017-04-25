package modeselection;

import java.util.ArrayList;

public interface ConditionCounted {
	public int numConditions();
	
	static public int numCombos(ArrayList<? extends ConditionCounted> flaggers) {
		return flaggers.stream().map(f -> f.numConditions()).reduce(1, (x, y) -> x * y);
	}
}
