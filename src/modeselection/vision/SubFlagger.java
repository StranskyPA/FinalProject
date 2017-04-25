package modeselection.vision;

import modeselection.ConditionCounted;
import modeselection.SensedValues;
import modeselection.util.Logger;

public interface SubFlagger<C extends Enum<C>> extends ConditionCounted {	
	public void update(AdaptedYUYVImage img, SensedValues<C> conditions);
	public void log(Logger logger);
}
