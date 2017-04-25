package modeselection.planning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.lcd.LCD;
import modeselection.SensedValues;
import modeselection.StateClassifier;
import modeselection.util.CycleTracker;
import modeselection.util.Logger;
import modeselection.util.Util;

public class GoalPicker<C extends Enum<C>, M extends Enum<M>> {
	private Planner<C,M> planner;
	private Executor<C,M> executor;
	private ArrayList<C> allPossibleGoals;
	private StateClassifier<C> sensors;
	private Logger logger = Logger.EV3Log;
	
	public GoalPicker(Planner<C,M> planner, Executor<C,M> executor, StateClassifier<C> sensors)  {
		this.planner = planner;
		this.executor = executor;
		this.sensors = sensors;
		allPossibleGoals = planner.getAllPossibleGoals().stream().collect(Collectors.toCollection(ArrayList::new));
	}
	
	public void control() throws IOException {
		while (Button.ESCAPE.isUp()) {
			Optional<C> goal = showGoals();
			if (goal.isPresent()) {
				logger.format("Selected goal: %s", goal.get().name());
				goalSelectedLoop(goal.get());
			} else {
				break;
			}
		}
		sensors.close();
	}
	
	void enterPause(String msg, Object... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		logger.log(msg);
		LCD.drawString(msg, 0, 2);
		LCD.drawString("Press ENTER", 0, 3);
		while (Button.ENTER.isUp());
		while (Button.ENTER.isDown());
		LCD.clear();				
	}
	
	void goalSelectedLoop(C goal) {
		CycleTracker cycles = new CycleTracker();
		while (Button.ESCAPE.isUp()) {
			SensedValues<C> state = sensors.getCurrentState();
			sensors.logState(logger, state);
			if (state.contains(goal)) {
				Util.stopAllMotors();
				enterPause("Goal %s achieved; %4.2f hz", goal.name(), cycles.getFPS());
				return;
			}
			Optional<Plan<C,M>> p = planner.depthFirstSearch(state, goal);
			if (p.isPresent()) {
				logger.log(p.get().toString());
				executor.executeFirstAction(p, state);
			} else {
				enterPause("No plan exists");
				return;
			}
			cycles.cycle();
		}
		enterPause(String.format("Goal %s abandoned", goal.name()));
	}
 	
	Optional<C> showGoals() {
		int goal = 0;
		LCD.drawString(String.format("Goal:%s     ", allPossibleGoals.get(goal).name()), 0, 2);
		for (;;) {
			if (Button.ESCAPE.isDown()) {
				return Optional.empty();
			} else if (Button.ENTER.isDown()) {
				return Optional.of(allPossibleGoals.get(goal));
			} else if (Button.LEFT.isDown()) {
				goal = changeGoal(Button.LEFT, goal, -1);
			} else if (Button.UP.isDown()) {
				goal = changeGoal(Button.UP, goal, -1);
			} else if (Button.RIGHT.isDown()) {
				goal = changeGoal(Button.RIGHT, goal, 1);
			} else if (Button.DOWN.isDown()) {
				goal = changeGoal(Button.DOWN, goal, 1);
			}
		}
	}
	
	public int changeGoal(Key buttonPressed, int goal, int offset) {
		goal = (goal + offset + allPossibleGoals.size()) % allPossibleGoals.size();
		LCD.drawString(String.format("Goal:%s     ", allPossibleGoals.get(goal).name()), 0, 2);
		while (buttonPressed.isDown()) {}
		return goal;
	}
}
