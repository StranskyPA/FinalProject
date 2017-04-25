package modeselection.fuzzy;

import modeselection.SensedValues;

public class Fuzzy {
	private Fuzzy() {}
	
	// Encoders
	public static double rising(double value, double start, double end) {
		return value > end ? 1.0 : value < start ? 0.0 : (value - start) / (end - start);
	}
	
	public static double falling(double value, double start, double end) {
		return value > end ? 0.0 : value < start ? 1.0 : (end - value) / (end - start);
	}
	
	public static double triangle(double value, double start, double peak, double end) {
		return value > end ? 0.0 : value < start ? 0.0 : value > peak ? (end - value) / (end - peak) : (value - start) / (peak - start);
	}
	
	public static double trapezoid(double value, double start, double peakStart, double peakEnd, double end) {
		return value > end ? 0.0 : value < start ? 0.0 : value > peakStart && value < peakEnd ? 1.0 : value >= peakEnd ? (end - value) / (end - peakEnd) : (value - start) / (peakStart - start);
	}
	
	// Operators
	public static <C extends Enum<C>> double or(SensedValues<C> sensed, C one, C two) {
		return Math.max(sensed.getValueFor(one), sensed.getValueFor(two));
	}

	public static <C extends Enum<C>> double and(SensedValues<C> sensed, C one, C two) {
		return Math.min(sensed.getValueFor(one), sensed.getValueFor(two));
	}
	
	public static <C extends Enum<C>> double not(SensedValues<C> sensed, C flag) {
		return 1.0 - sensed.getValueFor(flag);
	}
}
