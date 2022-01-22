package caceresenzo.apps.barcutoptimizer.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithm;

public class BarReference {
	
	/* Variables */
	private final String name;
	private final List<CutGroup> cutGroups;
	
	public BarReference(String name, List<CutGroup> cutGroups) {
		this.name = name;
		this.cutGroups = cutGroups;
	}
	
	/** @return Reference's name. */
	public String getName() {
		return name;
	}
	
	/** @return The {@link List} of {@link CutGroup} of this reference. */
	public List<CutGroup> getCutGroups() {
		return cutGroups;
	}
	
	/**
	 * Compute and get a {@link List} of all the {@link Cut} that are available in every {@link #getCutGroups() cut group} of this reference.
	 * 
	 * @return A, {@link Cut#sortByLengthAndAngles(List) sorted by length}, {@link List} of all the {@link Cut} for this reference.
	 * @see Cut#sortByLengthAndAngles(List)
	 * @see BarReference#getCutGroups()
	 */
	public List<Cut> getAllCuts() {
		List<Cut> allCuts = new ArrayList<>();
		
		getCutGroups().forEach((cutGroup) -> allCuts.addAll(cutGroup.getCuts()));
		Cut.sortByLengthAndAngles(allCuts);
		
		return allCuts;
	}
	
	public int countAllCuts() {
		return getCutGroups().stream().map(CutGroup::getCuts).mapToInt(List::size).sum();
	}
	
	public Map<Cut, Integer> computeCutCountMap() {
		Map<Cut, Integer> map = new LinkedHashMap<>();
		List<Cut> allCuts = getAllCuts();
		
		for (Cut cut : allCuts) {
			int countOfThisCut = 0;
			Cut deepedCut = null;
			
			for (Cut deepCut : map.keySet()) {
				if (deepCut.hasSameCutProperties(cut)) {
					countOfThisCut = map.get(deepedCut = deepCut);
					break;
				}
			}
			
			map.put(deepedCut != null ? deepedCut : cut, countOfThisCut + 1);
		}
		
		return map;
	}
	
	public Map<Double, Integer> computeRemainingCountMap(Supplier<Map<Double, Integer>> mapFactory) {
		Map<Double, Integer> map = mapFactory.get();
		
		for (CutGroup cutGroup : getCutGroups()) {
			map.compute(cutGroup.getRemainingBarLength(), (key, value) -> value == null ? 1 : value + 1);
		}
		
		return map;
	}
	
	public UnoptimizedCutList toUnoptimizedCutList(double barLength) {
		List<Cut> cuts = new ArrayList<>();
		Map<Cut, Integer> countMap = computeCutCountMap();
		
		for (Entry<Cut, Integer> entry : countMap.entrySet()) {
			Cut cut = entry.getKey();
			int count = entry.getValue();
			
			for (int i = 0; i < count; i++) {
				cuts.add(cut.clone());
			}
		}
		
		return new UnoptimizedCutList(barLength, cuts);
	}
	
	public void optimize(CutAlgorithm cutAlgorithm, double barLength) throws Exception {
		optimize(toUnoptimizedCutList(barLength), cutAlgorithm);
	}
	
	public void optimize(UnoptimizedCutList unoptimizedCutList, CutAlgorithm cutAlgorithm) throws Exception {
		List<CutGroup> optimizedCutGroups = cutAlgorithm.optimize(unoptimizedCutList);
		
		cutGroups.clear();
		if (optimizedCutGroups != null) {
			cutGroups.addAll(optimizedCutGroups);
		}
	}
	
	public static int countAllCutInList(List<BarReference> barReferences) {
		int count = 0;
		
		for (BarReference barReference : barReferences) {
			count += barReference.getAllCuts().size();
		}
		
		return count;
	}
	
	public static int countAllCutGroupInList(List<BarReference> barReferences) {
		int count = 0;
		
		for (BarReference barReference : barReferences) {
			count += barReference.getCutGroups().size();
		}
		
		return count;
	}
	
}