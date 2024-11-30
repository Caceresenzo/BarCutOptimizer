package dev.caceresenzo.barcutoptimizer.logic.algorithms;

import java.util.List;

import dev.caceresenzo.barcutoptimizer.models.CutGroup;
import dev.caceresenzo.barcutoptimizer.models.UnoptimizedCutList;

public interface CutAlgorithm {
	
	public List<CutGroup> optimize(UnoptimizedCutList unoptimizedCutList);
	
	public String getTranslationKey();
	
}