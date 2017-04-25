package modeselection.fuzzy;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import modeselection.SensedValues;

public class Defuzzifier<C extends Enum<C>> implements Consumer<SensedValues<C>> {
	private EnumMap<C,DoubleConsumer> defuzzers;
	
	public Defuzzifier(Class<C> conditionClass) {
		defuzzers = new EnumMap<>(conditionClass);
	}
	
	@Override
	public void accept(SensedValues<C> t) {
		for (Entry<C, DoubleConsumer> act: defuzzers.entrySet()) {
			act.getValue().accept(t.getValueFor(act.getKey()));
		}
	}

	public Defuzzifier<C> addDefuzzer(C condition, int start, int end, IntConsumer action) {
		defuzzers.put(condition, value -> {
			action.accept(defuzzify(value, start, end));
		});
		return this;
	}
	
	protected final static int defuzzify(double fuzzy, int speed0, int speed1) {
		if (speed0 > speed1) {
			return defuzzify(1.0 - fuzzy, speed1, speed0);
		} else {
			return (int)(speed0 + fuzzy * (speed1 - speed0));
		}
	}
}
