package modeselection.util;

import java.util.EnumMap;
import java.util.Map.Entry;

import lejos.hardware.lcd.LCD;

public class EnumHistogram<T extends Enum<T>> extends SemiAbstractHistogram<T,EnumMap<T,Integer>> implements DeepCopyable<EnumHistogram<T>> {
	private Class<T> enumType;
	
	private static final String JOINER = ":";
	
	public EnumHistogram(Class<T> enumType) {
		super(new EnumMap<T,Integer>(enumType));
		this.enumType = enumType;
	}
	
	public static <T extends Enum<T>> EnumHistogram<T> from(Class<T> enumType, String src) {
		EnumHistogram<T> result = new EnumHistogram<>(enumType);
		for (String part: Util.debrace(src)) {
			String[] pieces = part.split(JOINER);
			result.setCountFor(Util.toEnum(enumType, pieces[0]), 
					Integer.parseInt(pieces[1]));
		}
		return result;
	}
	
	public EnumMap<T,Double> getPortions() {
		EnumMap<T,Double> result = new EnumMap<T,Double>(enumType);
		super.getPortions(result);
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Entry<T, Integer> entry: this) {
			result.append("{" + entry.getKey() + JOINER + entry.getValue() + "}");
		}
		return result.toString();
	}
	
	public void toLCD() {
		LCD.clearDisplay();
		int line = 0;
		for (Entry<T, Integer> entry: this) {
			LCD.drawString(String.format("%s:%d", entry.getKey().toString(), entry.getValue()), 0, line++);
		}
	}
	
	public static int minDistinctPrefixSize(String s1, String s2) {
		int target = Math.min(s1.length(), s2.length());
		int result = 1;
		while (result <= target && s1.charAt(result - 1) == s2.charAt(result - 1)) {
			result += 1;
		}
		return result;
	}
	
	public static <T extends Enum<T>> int findMinDistinctPrefixSizeFor(T candidate, Class<T> type) {
		int max = 0;
		for (T other: type.getEnumConstants()) {
			if (!other.equals(candidate)) {
				max = Math.max(max, minDistinctPrefixSize(candidate.toString(), other.toString()));
			}
		}
		return max;
	}
	
	public static <T extends Enum<T>> EnumHistogram<T> fromString(Class<T> enumType, String src) {
		EnumHistogram<T> result = new EnumHistogram<T>(enumType);
		String[] pairs = src.substring(1, src.length() - 1).split(", ");
		for (String pair: pairs) {
			if (pair.length() > 0) {
				String[] kv = pair.split("=");
				result.setCountFor(Enum.valueOf(enumType,kv[0]), Integer.parseInt(kv[1]));
			}
		}
		return result;
	}

	@Override
	public EnumHistogram<T> deepCopy() {
		EnumHistogram<T> result = new EnumHistogram<>(enumType);
		for (Entry<T, Integer> pair: this) {
			result.setCountFor(pair.getKey(), pair.getValue());
		}
		return result;
	}
}
