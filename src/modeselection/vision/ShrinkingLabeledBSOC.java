package modeselection.vision;

import java.util.ArrayList;

import modeselection.cluster.LabeledBSOC;
import modeselection.util.EnumHistogram;
import modeselection.util.Util;

public class ShrinkingLabeledBSOC<E extends Enum<E>> {
	private int shrink;
	private LabeledBSOC<AdaptedYUYVImage,E> bsoc;
	
	public ShrinkingLabeledBSOC(Class<E> enumClass, int maxNumNodes, E startLabel, int shrink) {
		bsoc = new LabeledBSOC<>(enumClass, maxNumNodes, startLabel);
		this.shrink = shrink;
	}
	
	public void train(AdaptedYUYVImage example, E label) {
		bsoc.train(example.shrunken(shrink), label);
	}
	
	public E bestMatchFor(AdaptedYUYVImage example) {
		return bsoc.bestMatchFor(example.shrunken(shrink));
	}
	
	public EnumHistogram<E> getCountsFor(AdaptedYUYVImage example) {
		return bsoc.getCountsFor(example.shrunken(shrink));
	}
	
	public ShrinkingLabeledBSOC(Class<E> enumClass, String src) {
		ArrayList<String> parts = Util.debrace(src);
		bsoc = new LabeledBSOC<>(enumClass, parts.get(0), AdaptedYUYVImage::fromString);
		shrink = Integer.parseInt(parts.get(1));
	}
	
	@Override
	public String toString() {
		return '{' + bsoc.toString() + "}{" + shrink + "}";
	}
}
