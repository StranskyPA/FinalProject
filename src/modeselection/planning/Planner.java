package modeselection.planning;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Map.Entry;

import modeselection.SensedValues;

import java.util.Set;

public class Planner<C extends Enum<C>, M extends Enum<M>> {
	private EnumMap<C,EnumMap<C,M>> stateActionGraph;
	private Class<C> condClass;
	private EnumSet<C> allPossibleGoals, allPossibleStarts;

	public Planner(Class<C> condClass) {
		stateActionGraph = new EnumMap<>(condClass);
		this.condClass = condClass;
		allPossibleGoals = EnumSet.noneOf(condClass);
		allPossibleStarts = EnumSet.noneOf(condClass);
	}
	
	@SafeVarargs
	public final Planner<C,M> add(C startState, M transition, C... endStates) {
		if (!stateActionGraph.containsKey(startState)) {
			stateActionGraph.put(startState, new EnumMap<>(condClass));
		}
		for (C endState: endStates) {
			stateActionGraph.get(startState).put(endState, transition);
			allPossibleStarts.add(startState);
			allPossibleGoals.add(endState);
		}
		return this;
	}
	
	public Set<C> getAllPossibleGoals() {
		return Collections.unmodifiableSet(allPossibleGoals);
	}
	
	public Set<C> getAllPossibleStartStates() {
		return Collections.unmodifiableSet(allPossibleStarts);
	}
	
	public Optional<Plan<C,M>> depthFirstSearch(C start, C goal) {
		return depthFirstSearch(start, goal, EnumSet.noneOf(condClass));
	}
	
	private Optional<Plan<C,M>> depthFirstSearch(C start, C goal, EnumSet<C> triedFrom) {
		if (stateActionGraph.containsKey(start) && !triedFrom.contains(start)) {
			for (Entry<C, M> next: stateActionGraph.get(start).entrySet()) {
				if (next.getKey() == goal) {
					return Optional.of(new Plan<>(next.getValue(), next.getKey()));
				} else {
					EnumSet<C> triedFromNext = triedFrom.clone();
					triedFromNext.add(start);
					Optional<Plan<C,M>> suffix = depthFirstSearch(next.getKey(), goal, triedFromNext);
					if (suffix.isPresent() && !suffix.get().contains(next.getKey())) {
						return Optional.of(new Plan<>(next.getValue(), next.getKey(), suffix));
					}
				}
			}
		} 
		return Optional.empty();		
	}
	
	public Optional<Plan<C,M>> depthFirstSearch(SensedValues<C> state, C goal) {
		for (C flag: state.flagsOnly()) {
			if (allPossibleStarts.contains(flag)) {
				return depthFirstSearch(flag, goal);
			}
		}
		return Optional.empty();
	}
}
