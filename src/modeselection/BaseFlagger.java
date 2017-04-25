package modeselection;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import modeselection.util.Logger;

abstract public class BaseFlagger<C extends Enum<C>, D> implements ConditionCounted {
	private ArrayList<C> flags = new ArrayList<>();
	private ArrayList<Predicate<D>> preds = new ArrayList<>();
	private ArrayList<C> valueFlags = new ArrayList<>();
	private ArrayList<Function<D,Double>> valueFuncs = new ArrayList<>();
	
	public BaseFlagger<C,D> add(C flag, Predicate<D> p) {
		flags.add(flag);
		preds.add(p);
		return this;
	}
	
	public BaseFlagger<C,D> add2(C trueFlag, C falseFlag, Predicate<D> p) {
		return add(trueFlag, p)
			  .add(falseFlag, d -> !p.test(d));
	}
	
	public BaseFlagger<C,D> addFloatValue(C valueFlag, Function<D,Float> valueFunc) {
		return addValue(valueFlag, f -> (double)valueFunc.apply(f));
	}
	
	public BaseFlagger<C,D> addValue(C valueFlag, Function<D,Double> valueFunc) {
		valueFlags.add(valueFlag);
		valueFuncs.add(valueFunc);
		return this;
	}

	public BaseFlagger<C,D> addIntValue(C valueFlag, Function<D,Integer> valueFunc) {
		return addValue(valueFlag, d -> (double)valueFunc.apply(d));
	}

	public void update(D sample, SensedValues<C> conditions) {
		for (int i = 0; i < flags.size(); i++) {
			if (preds.get(i).test(sample)) {
				conditions.add(flags.get(i));
			}
		}
		for (int i = 0; i < valueFlags.size(); i++) {
			conditions.rawValue(valueFlags.get(i), valueFuncs.get(i).apply(sample));
		}
	}

	public void log(Logger logger) {
		logger.log(getLogMsg());
	}

	@Override
	public int numConditions() {
		return flags.size();
	}
	
	abstract protected String getLogMsg();
}
