package modeselection.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import modeselection.util.Duple;
import modeselection.util.Util;

public interface Clusterer<T extends Measurable<T>> {
	public int train(T example);
	
	default public int getClosestMatchFor(T example) {
		return getClosestNodeDistanceFor(example).getFirst();
	}
	
	default public Duple<Integer,Long> getClosestNodeDistanceFor(T example) {
		Util.assertState(size() > 0, "No nodes exist");
		Util.assertArgument(example != null, "Null example given!");
		Duple<Integer,Long> result = null;
		for (int id: getClusterIds()) {
			long dist = example.distanceTo(getIdealInputFor(id));
			if (result == null || dist < result.getSecond()) {
				result = new Duple<>(id, dist);
			}
		}
		return result;
	}
	
	default public ArrayList<Duple<Integer,Long>> getNodeRanking(T example) {
		ArrayList<Duple<Integer, Long>> result = new ArrayList<>();
		for (int id: getClusterIds()) {
			result.add(new Duple<>(id, example.distanceTo(getIdealInputFor(id))));
		}
		Collections.sort(result, (o1, o2) -> o1.getSecond() < o2.getSecond() ? -1 : o1.getSecond() > o2.getSecond() ? 1 : 0);
		return result;
	}
	
	public T getIdealInputFor(int node);
	
	public int size();
	
	public Collection<Integer> getClusterIds();
}
