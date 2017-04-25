package modeselection.vision.landmarks;

import java.util.function.Predicate;

public class LandmarkPredicate<C extends Enum<C>> {
	private Predicate<Long> checker;
	private C flag;
	
	public LandmarkPredicate(Predicate<Long> checker, C flag) {
		this.checker = checker;
		this.flag = flag;
	}
	
	public boolean matches(long distance) {
		return checker.test(distance);
	}
	
	public C getFlag() {return flag;}
}
