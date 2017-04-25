package modeselection.planning.test;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import modeselection.planning.Plan;
import modeselection.planning.Planner;

public class PlannerTest {

	@Test
	public void test() {
		Planner<Condition,Mode> planner = new Planner<>(Condition.class);
        planner.add(Condition.CARPET, Mode.FORWARD, Condition.STAIRS, Condition.PAPER)
               .add(Condition.PAPER, Mode.RIGHT, Condition.CARPET)
               .add(Condition.STAIRS, Mode.LEFT, Condition.CARPET)
               .add(Condition.TLEFT, Mode.RIGHT, Condition.CARPET, Condition.PAPER, Condition.STAIRS)
               .add(Condition.TRIGHT, Mode.LEFT, Condition.CARPET, Condition.PAPER, Condition.STAIRS);
        assess(planner, "[LEFT;CARPET]", Condition.STAIRS, Condition.CARPET);
        assess(planner, "[FORWARD;STAIRS]", Condition.CARPET, Condition.STAIRS);
        assess(planner, "[RIGHT;CARPET][FORWARD;STAIRS]", Condition.PAPER, Condition.STAIRS);
        assess(planner, "[LEFT;CARPET][FORWARD;PAPER]", Condition.STAIRS, Condition.PAPER);
	}

	private void assess(Planner<Condition,Mode> planner, String target, Condition start, Condition goal) {
        Optional<Plan<Condition,Mode>> one = planner.depthFirstSearch(start, goal);
        one.ifPresent(p -> System.out.println(p));
        assertEquals(target, one.get().toString());
	}
}
