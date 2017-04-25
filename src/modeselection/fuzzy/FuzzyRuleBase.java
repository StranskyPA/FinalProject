package modeselection.fuzzy;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.Function;

import modeselection.Flagger;
import modeselection.SensedValues;

public class FuzzyRuleBase<C extends Enum<C>> implements Flagger<C> {
	private EnumMap<C,Function<SensedValues<C>,Double>> rules;
	
	public FuzzyRuleBase(Class<C> conditionClass) {
		rules = new EnumMap<>(conditionClass);
	}
	
	public void addRule(C condition, Function<SensedValues<C>,Double> rule) {
		rules.put(condition, rule);
	}

	@Override
	public int numConditions() {
		return rules.size();
	}

	@Override
	public void update(SensedValues<C> conditions) {
		for (Entry<C, Function<SensedValues<C>, Double>> condition: rules.entrySet()) {
			conditions.rawValue(condition.getKey(), condition.getValue().apply(conditions));
		}
	}
}
