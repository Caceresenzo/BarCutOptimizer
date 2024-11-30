package dev.caceresenzo.barcutoptimizer.optimize;

import java.util.List;

import dev.caceresenzo.barcutoptimizer.model.CutGroup;
import dev.caceresenzo.barcutoptimizer.model.UnoptimizedCutList;

public interface CutAlgorithm {

	public List<CutGroup> optimize(UnoptimizedCutList unoptimizedCutList);

	public String getTranslationKey();

}