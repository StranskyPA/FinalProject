package modeselection.planning;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Consumer;

import modeselection.SensedValues;

public class Executor<C extends Enum<C>, M extends Enum<M>> {
	private EnumMap<M,Consumer<SensedValues<C>>> subs;
	private EnumMap<M,Runnable> starters;
	private M prevMode;
	
	public Executor(Class<M> modeClass, M startMode) {
		subs = new EnumMap<>(modeClass);
		starters = new EnumMap<>(modeClass);
		prevMode = startMode;
	}
	
	public Executor<C,M> mode(M mode, Runnable actionStart) {
		starters.put(mode, actionStart);
		return this;
	}
	
	public Executor<C,M> sub(M mode, Consumer<SensedValues<C>> action) {
		subs.put(mode, action);
		return this;
	}
	
	public void executeFirstAction(Optional<Plan<C,M>> plan, SensedValues<C> state) {
		plan.ifPresent(p -> {
			M pMode = p.getAction();
			if (pMode != prevMode) {
				if (starters.containsKey(pMode)) {
					starters.get(pMode).run();
				}
				prevMode = pMode;
			}
			if (subs.containsKey(pMode)) {
				subs.get(pMode).accept(state);
			}
		});
	}
}
