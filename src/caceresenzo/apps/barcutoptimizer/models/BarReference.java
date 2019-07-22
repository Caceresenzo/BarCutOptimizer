package caceresenzo.apps.barcutoptimizer.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarReference {
	
	private final String name;
	private final List<CutGroup> cutGroups;
	
	public BarReference(String name, List<CutGroup> cutGroups) {
		this.name = name;
		this.cutGroups = cutGroups;
	}
	
	public String getName() {
		return name;
	}
	
	public List<CutGroup> getCutGroups() {
		return cutGroups;
	}
	
	public List<Cut> getAllCuts() {
		List<Cut> allCuts = new ArrayList<>();
		
		getCutGroups().forEach((cutGroup) -> allCuts.addAll(cutGroup.getCuts()));
		
		return allCuts;
	}
	
	public Map<Cut, Integer> computeCutCountMap() {
		Map<Cut, Integer> map = new HashMap<>();
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
}