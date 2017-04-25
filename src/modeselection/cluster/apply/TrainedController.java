package modeselection.cluster.apply;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

import modeselection.SensedValues;
import modeselection.cluster.train.RobotKey;
import modeselection.util.Util;
import modeselection.vision.ShrinkingLabeledBSOC;

public class TrainedController<C extends Enum<C>, M extends Enum<M> & RobotKey> implements Consumer<SensedValues<C>> {

	private ShrinkingLabeledBSOC<M> bsoc;
	
	public TrainedController(Class<M> mClass, String filename) throws FileNotFoundException {
		bsoc = Util.fileToObject(new File(filename), s -> new ShrinkingLabeledBSOC<>(mClass, s));
	}
	
	@Override
	public void accept(SensedValues<C> t) {
		M action = bsoc.bestMatchFor(t.getRawImage());
		action.act();
	}
}
