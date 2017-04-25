package modeselection;

import java.io.IOException;
import java.util.EnumMap;
import java.util.function.Consumer;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import modeselection.util.CycleTracker;
import modeselection.util.Logger;

public class ModeSelector<C extends Enum<C>, M extends Enum<M>> implements Runnable, Consumer<SensedValues<C>> {
	private M current;
	private SensedValues<C> conditions;
	private StateClassifier<C> sensors;
	private EnumMap<M,Consumer<SensedValues<C>>> subs;
	private EnumMap<M,Runnable> actions;
	private EnumMap<M,Transitions<C,M>> modeTransitions;
	private Logger logger = Logger.EV3Log;
	private CycleTracker cycles;
	
	public ModeSelector(Class<C> conditionClass, Class<M> modeClass, M start) {
		this.current = start;
		conditions = new SensedValues<>(conditionClass);
		subs = new EnumMap<>(modeClass);
		actions = new EnumMap<>(modeClass);
		modeTransitions = new EnumMap<>(modeClass);
		sensors = new StateClassifier<>(conditionClass);
	}
	
	public ModeSelector<C,M> flagger(Flagger<C> flagger) {
		sensors.add(flagger);
		return this;
	}
	
	public ModeSelector<C,M> sensor(SensorFlagger<C> flagger) {
		return flagger(flagger);
	}
	
	public ModeSelector<C,M> mode(M mode, Transitions<C,M> transitions, Runnable action) {
		checkHas(mode);		
		actions.put(mode, action);
		modeTransitions.put(mode, transitions);
		return this;
	}
	
	public ModeSelector<C,M> subSelector(M mode, Transitions<C,M> transitions, Consumer<SensedValues<C>> subSelector) {
		checkHas(mode);
		subs.put(mode, subSelector);
		modeTransitions.put(mode, transitions);
		return this;
	}
	
	public boolean has(M mode) {
		return subs.containsKey(mode) || actions.containsKey(mode);
	}
	
	public void checkHas(M mode) {
		if (has(mode)) {
			throw new IllegalStateException("Mode " + mode.name() + " already added");
		}
	}
	
	public void control() throws IOException {
		actions.get(current).run();
		cycles = new CycleTracker();
		while (Button.ESCAPE.isUp()) {
			run();
			show();
			log();
		}
		sensors.close();
	}

	@Override
	public void run() {
		accept(sensors.getCurrentState());
		cycles.cycle();
	}

	@Override
	public void accept(SensedValues<C> sensed) {
		conditions = sensed;
		M prev = current;
		current = modeTransitions.get(current).getMode(current, conditions);
		if (actions.containsKey(current)) {
			if (prev != current) {
				actions.get(current).run();
			}
		} else if (subs.containsKey(current)) {
			subs.get(current).accept(conditions);
		} else {
			throw new IllegalStateException("Mode " + current.name() + " not defined");
		}
	}
	
	public void show() {
		LCD.clear();
		LCD.drawString(current.name(), 0, 0);
		LCD.drawString(cycles.getFPSString() + " hz", 0, 1);
		StateClassifier.showState(conditions, 2);
	}
	
	public void log() {
		logger.format("current: %s", current.name());
		logger.format("%s hz", cycles.getFPSString());
		sensors.logState(logger, conditions);
	}
}
