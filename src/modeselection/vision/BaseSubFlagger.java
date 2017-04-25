package modeselection.vision;

import modeselection.BaseFlagger;
import modeselection.SensedValues;

abstract public class BaseSubFlagger<C extends Enum<C>, D> extends BaseFlagger<C,D> implements SubFlagger<C> {
	private D lastSample = null;
	
	abstract public D getSample(AdaptedYUYVImage img);
	
	@Override
	public void update(AdaptedYUYVImage img, SensedValues<C> conditions) {
		lastSample = getSample(img);
		update(lastSample, conditions);
	}

	@Override
	protected String getLogMsg() {
		return lastSample.toString();
	}
}
