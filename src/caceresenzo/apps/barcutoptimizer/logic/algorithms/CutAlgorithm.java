package caceresenzo.apps.barcutoptimizer.logic.algorithms;

import java.util.List;

import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;

public interface CutAlgorithm {
	
	public List<CutGroup> optimize(UnoptimizedCutList unoptimizedCutList);
	
	public String getTranslationKey();
	
}