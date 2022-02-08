package caceresenzo.apps.barcutoptimizer.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BarReference {
	
	/* Variables */
	private final String name;
	private final List<CutGroup> cutGroups;
	
	public List<Cut> getAllCuts() {
		return getCutGroups()
				.stream()
				.map(CutGroup::getCuts)
				.flatMap(List::stream)
				.sorted()
				.collect(Collectors.toList());
	}
	
	public int countAllCuts() {
		return getCutGroups()
				.stream()
				.map(CutGroup::getCuts)
				.mapToInt(List::size)
				.sum();
	}
	
	public Map<Cut, Integer> computeCutCountMap() {
		Map<Cut, Integer> map = new LinkedHashMap<>();
		List<Cut> allCuts = getAllCuts();
		
		for (Cut cut : allCuts) {
			int countOfThisCut = 0;
			Cut deepedCut = null;
			
			for (Cut deepCut : map.keySet()) {
				if (deepCut.equals(cut)) {
					deepedCut = deepCut;
					countOfThisCut = map.get(deepedCut);
					break;
				}
			}
			
			map.put(deepedCut != null ? deepedCut : cut, countOfThisCut + 1);
		}
		
		return map;
	}
	
	public Map<Double, Integer> computeRemainingCountMap(Map<Double, Integer> map) {
		for (CutGroup cutGroup : getCutGroups()) {
			map.compute(cutGroup.getRemainingBarLength(), (key, value) -> value == null ? 1 : value + 1);
		}
		
		return map;
	}
	
	public void optimize(UnoptimizedCutList unoptimizedCutList, CutAlgorithm cutAlgorithm) throws Exception {
		List<CutGroup> optimizedCutGroups = cutAlgorithm.optimize(unoptimizedCutList);
		
		cutGroups.clear();
		if (optimizedCutGroups != null) {
			cutGroups.addAll(optimizedCutGroups);
		}
	}
	
	public static int countAllCutGroup(List<BarReference> barReferences) {
		return barReferences
				.stream()
				.map(BarReference::getCutGroups)
				.mapToInt(List::size)
				.sum();
	}
	
}