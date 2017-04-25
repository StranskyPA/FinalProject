package modeselection;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import lejos.hardware.lcd.LCD;
import modeselection.util.Logger;

public class StateClassifier<C extends Enum<C>> implements Closeable {
	private ArrayList<Flagger<C>> flaggers = new ArrayList<>();
	private ArrayList<Closeable> toClose = new ArrayList<>();
	private Class<C> conditionClass;
	
	public StateClassifier(Class<C> conditionClass) {
		this.conditionClass = conditionClass;
	}
	
	public StateClassifier<C> add(Flagger<C> flagger) {
		flaggers.add(flagger);
		if (flagger instanceof Closeable) {
			toClose.add((Closeable)flagger);
		}
		return this;
	}
	
	public int numPossibleStates() {
		return ConditionCounted.numCombos(flaggers);
	}
	
	public SensedValues<C> getCurrentState() {
		SensedValues<C> result = new SensedValues<>(conditionClass);
		for (Flagger<C> flagger: flaggers) {
			flagger.update(result);
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		for (Closeable c: toClose) {
			c.close();
		}
	}
	
	public void logState(Logger logger, SensedValues<C> state) {
		logger.log("state:");
		for (String cond: state.getStateReport()) {
			logger.log(cond);
		}
		for (Flagger<C> sensor: flaggers) {
			sensor.log(logger);
		}
	}
	
	public static <C extends Enum<C>> void showState(SensedValues<C> state, int row) {
		for (String cond: state.getStateReport()) {
			LCD.drawString(cond, 0, row++);
		}
	}
}
