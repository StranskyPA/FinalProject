package modeselection.qlearning;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import modeselection.SensedValues;
import modeselection.StateClassifier;
import modeselection.util.Logger;

public class QController<C extends Enum<C>, M extends Enum<M>> implements Runnable, Consumer<SensedValues<C>> {
	private QTable<C,M> table;
	private EnumMap<M,Runnable> actions;
	private BiFunction<Set<C>,M,Double> rewarder;
	private StateClassifier<C> sensors;
	private double discount;
	private Class<M> actionClass;
	
	private SensedValues<C> lastState = null;
	private double lastReward = 0.0;
	private M lastAction = null;
	
	private Logger logger = Logger.EV3Log;
	
	private int cycle, halfLife;
	
	public double learningRate() {
		return (double)halfLife / (halfLife + cycle);
	}
	
	public QController(Class<M> actionClass) {
		actions = new EnumMap<>(actionClass);
		this.actionClass = actionClass;
		cycle = 0;
	}
	
	public QController<C,M> targetVisits(int targetVisits) {
		table = new QTable<C,M>(actionClass, targetVisits);
		return this;
	}
	
	public QController<C,M> action(M action, Runnable code) {
		if (lastAction == null) {
			lastAction = action;
		}
		actions.put(action, code);
		return this;
	}

	public QController<C,M> halfLife(int halfLife) {
		this.halfLife = halfLife;
		return this;
	}
	
	public QController<C,M> discount(double discount) {
		this.discount = discount;
		return this;
	}

	public QController<C,M> rewards(BiFunction<Set<C>,M,Double> rewarder) {
		this.rewarder = rewarder;
		return this;
	}
	
	public QController<C,M> sensors(StateClassifier<C> factory) {
		this.sensors = factory;
		return this;
	}
	
	@Override
	public void run() {
		accept(sensors.getCurrentState());
	}
	
	public void control() throws IOException {
		actions.get(lastAction).run();
		while (Button.ESCAPE.isUp()) {
			run();
			show();
			log();
		}
		sensors.close();
		logger.log("QTable");
		logger.log(table.toString());
	}
	
	public void show() {
		LCD.clear();
		LCD.drawString(lastAction.name(), 0, 0);
		LCD.drawString(String.format("cycle: %d",  cycle), 0, 1);
		StateClassifier.showState(lastState, 2);
	}
	
	public void log() {
		logger.format("current: %s", lastAction.name());
		sensors.logState(logger, lastState);
	}

	@Override
	public void accept(SensedValues<C> state) {
		double reward = rewarder.apply(state.flagsOnly(), lastAction);
		if (lastState != null) {
			QCell<M> prev = table.get(lastState.flagsOnly());
			QCell<M> now = table.get(state.flagsOnly());
			double learningRate = learningRate();
			double oldQ = (1 - learningRate) * prev.getQFor(lastAction);
			double update = learningRate * (discount * now.getMaxQ() + lastReward);
			prev.setQFor(lastAction, oldQ + update);
		}
		
		M action = table.get(state.flagsOnly()).getBest();
		if (lastAction != action) {
			actions.get(action).run();
		}
		
		lastState = state;
		lastReward = reward;
		lastAction = action;
		cycle += 1;
	}
}
