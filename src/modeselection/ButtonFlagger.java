package modeselection;

import java.util.EnumMap;

import lejos.hardware.Key;

public class ButtonFlagger<C extends Enum<C>> implements Flagger<C> {
	private enum BState {
		UP {boolean match(Key k) {return k.isUp();}}, 
		DOWN {boolean match(Key k) {return k.isDown();}};
		abstract boolean match(Key k);
	};
	private Key button;
	private EnumMap<BState,C> flags;
	
	public ButtonFlagger(Key button) {
		this.button = button;
		flags = new EnumMap<>(BState.class);
	}
	
	public ButtonFlagger<C> addDown(C ifDown) {
		flags.put(BState.DOWN, ifDown);
		return this;
	}
	
	public ButtonFlagger<C> addUp(C ifUp) {
		flags.put(BState.UP, ifUp);
		return this;
	}
	
	public ButtonFlagger<C> addUpDown(C ifUp, C ifDown) {
		return addUp(ifUp).addDown(ifDown);
	}
	 
	@Override
	public void update(SensedValues<C> conditions) {
		for (BState b: flags.keySet()) {
			if (b.match(button)) {
				conditions.add(flags.get(b));
			}
		}
	}

	@Override
	public int numConditions() {
		return flags.size();
	}
}
