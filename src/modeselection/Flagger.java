package modeselection;

import modeselection.util.Logger;

public interface Flagger<C extends Enum<C>> extends ConditionCounted {
	public void update(SensedValues<C> conditions);
	
	default public void log(Logger logger) {}
}
