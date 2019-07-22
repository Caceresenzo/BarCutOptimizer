package caceresenzo.apps.barcutoptimizer.models;

import java.util.Collections;
import java.util.List;

/**
 * Basically a {@link CutGroup} but with an unmodifiable list for the {@link #getCuts() list of cut}.<br>
 * This class contain {@link Cut} that will need to be optimize.
 * 
 * @author Enzo CACERES
 */
public class UnoptimizedCutList extends CutGroup {
	
	/* Constructor */
	public UnoptimizedCutList(double barLength, List<Cut> cuts) {
		super(barLength, -1, Collections.unmodifiableList(cuts));
	}
	
	/** @return An {@link Collections#unmodifiableList(List) unmodifiable list} of {@link Cut} that need to be optimized. */
	public List<Cut> getCuts() {
		return super.getCuts();
	}
	
	@Override
	public double getRemainingBarLength() {
		throw new IllegalStateException("Can't get the remaining bar length of an unoptimized cut list.");
	}
	
}