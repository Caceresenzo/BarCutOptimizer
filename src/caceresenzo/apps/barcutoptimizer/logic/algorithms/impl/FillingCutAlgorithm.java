package caceresenzo.apps.barcutoptimizer.logic.algorithms.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithm;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithmException;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations.AlgorithmSetting;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FillingCutAlgorithm implements CutAlgorithm {
	
	@AlgorithmSetting(key = "start-offset")
	private double startOffset = 50.0d;
	
	@AlgorithmSetting(key = "end-offset")
	private double endOffset = 50.0d;
	
	@AlgorithmSetting(key = "cut-offset")
	private double cutOffset = 10.0d;
	
	@Override
	public List<CutGroup> optimize(UnoptimizedCutList unoptimizedCutList) {
		final List<CutGroup> groups = new ArrayList<>();
		
		final double barLength = unoptimizedCutList.getBarLength();
		final List<Cut> cuts = new ArrayList<>(unoptimizedCutList.getCuts());
		Collections.sort(cuts);
		
		final double usableBarLength = barLength - startOffset - endOffset;
		if (usableBarLength <= 0) {
			throw new IllegalStateException("Usable bar length is under or equal to 0, can't continue.");
		}
		
		while (!cuts.isEmpty()) {
			List<Cut> barCuts = new ArrayList<>();
			double remainingBarLength = usableBarLength;
			
			ListIterator<Cut> iterator = cuts.listIterator();
			while (iterator.hasNext() && remainingBarLength > 0) {
				Cut cut = iterator.next();
				
				if (!cut.isFitting(usableBarLength)) {
					throw new CutAlgorithmException("Usable bar length is shorter than a cut.");
				}
				
				if (cut.getLength() + cutOffset <= remainingBarLength) {
					cut.setX(startOffset + usableBarLength - remainingBarLength);
					
					iterator.remove();
					barCuts.add(cut);
					
					remainingBarLength -= cut.getLength();
					remainingBarLength -= cutOffset;
					
					if (remainingBarLength < 0) {
						throw new CutAlgorithmException("Remaining bar length is negative.");
					}
				}
			}
			
			groups.add(new CutGroup(barLength, remainingBarLength, barCuts));
		}
		
		return groups;
	}
	
	@Override
	public String getTranslationKey() {
		return "filling";
	}
	
}