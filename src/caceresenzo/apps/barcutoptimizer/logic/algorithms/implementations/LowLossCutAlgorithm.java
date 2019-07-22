package caceresenzo.apps.barcutoptimizer.logic.algorithms.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithm;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;

public class LowLossCutAlgorithm implements CutAlgorithm {
	
	@AlgorithmSetting(key = "start-offset")
	public static final double START_OFFSET = 50.0d;
	
	@AlgorithmSetting(key = "end-offset")
	public static final double END_OFFSET = 50.0d;
	
	@AlgorithmSetting(key = "cut-offset")
	public static final double CUT_OFFSET = 10.0d;
	
	@Override
	public List<CutGroup> optimize(UnoptimizedCutList unoptimizedCutList) {
		final List<CutGroup> groups = new ArrayList<>();
		
		final double barLength = unoptimizedCutList.getBarLength();
		final List<Cut> cuts = new ArrayList<>(unoptimizedCutList.getCuts());
		
		final double usableBarLength = barLength - START_OFFSET - END_OFFSET;
		if (usableBarLength <= 0) {
			throw new IllegalStateException("Usable bar length is under or equal to 0, can't continue.");
		}
		
		while (!cuts.isEmpty()) {
			List<Cut> barCuts = new ArrayList<>();
			double remainingBarLength = usableBarLength;
			
			Iterator<Cut> iterator = cuts.iterator();
			while (iterator.hasNext() && remainingBarLength > 0) {
				Cut cut = iterator.next();
				
				if (cut.isFitting(remainingBarLength)) {
					iterator.remove();
					barCuts.add(cut);
					
					remainingBarLength -= cut.getLength();
				} else {
					break;
				}
			}
			
			groups.add(new CutGroup(barLength, remainingBarLength, barCuts));
		}
		
		return groups;
	}
	
	public static void main(String[] args) {
		UnoptimizedCutList unoptimizedCutList = new UnoptimizedCutList(6500.0, Arrays.asList(Cut.dummy(500, 90, 90), Cut.dummy(2500, 90, 90), Cut.dummy(2500, 90, 90)));
		
		new LowLossCutAlgorithm().optimize(unoptimizedCutList).forEach((group) -> System.out.println(group.getCuts()));
	}
	
}