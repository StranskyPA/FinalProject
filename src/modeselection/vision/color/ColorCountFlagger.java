package modeselection.vision.color;

import modeselection.vision.AdaptedYUYVImage;
import modeselection.vision.BaseSubFlagger;

public class ColorCountFlagger<C extends Enum<C>> extends BaseSubFlagger<C,ColorCount> {
	private ColorFilter filter;
	
	public ColorCountFlagger(int uLo, int uHi, int vLo, int vHi) {
		filter = new ColorFilter(uLo, uHi, vLo, vHi);
	}

	@Override
	public ColorCount getSample(AdaptedYUYVImage img) {
		return new ColorCount(filter, img);
	}
}
