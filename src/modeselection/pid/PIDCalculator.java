package modeselection.pid;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import modeselection.SensedValues;
import modeselection.util.Logger;

public class PIDCalculator<C extends Enum<C>> implements Consumer<SensedValues<C>>{
	private double lastError = 0, totalError = 0;
	
	private double tauP = 1, tauI = 0, tauD = 0, targetValue = 0;
	
	private C tag;
	private IntConsumer action;
	
	public PIDCalculator<C> targetValue(double targetValue) {
		this.targetValue = targetValue;
		return this;
	}
	
	public PIDCalculator<C> P(double p) {
		this.tauP = p;
		return this;
	}
	
	public PIDCalculator<C> I(double i) {
		this.tauI = i;
		return this;
	}
	
	public PIDCalculator<C> D(double d) {
		this.tauD = d;
		return this;
	}	
	
	public PIDCalculator<C> action(C tag, IntConsumer action) {
		this.tag = tag;
		this.action = action;
		return this;
	}
	
	public int apply(double value) {
		double error = targetValue - value;
		totalError += error;
		double result = tauP * error + tauI * totalError + tauD * (error - lastError);
		lastError = error;
		return (int)result;
	}

	@Override
	public void accept(SensedValues<C> sensed) {
		int correctedError = apply(sensed.getValueFor(tag));
		Logger.EV3Log.format("pid: %d", correctedError);
		action.accept(correctedError);
	}
}
