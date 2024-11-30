package dev.caceresenzo.barcutoptimizer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.caceresenzo.barcutoptimizer.ui.dialog.CutsEditionDialog;

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
	
	@Override
	public double getRemainingBarLength() {
		throw new IllegalStateException("Can't get the remaining bar length of an unoptimized cut list.");
	}
	
	public static UnoptimizedCutList fromCutTableInputs(List<CutTableInput> cutTableInputs, double barLength) {
		List<Cut> cuts = new ArrayList<>();
		
		for (CutTableInput cutTableInput : cutTableInputs) {
			if (cutTableInput.getLength() == CutsEditionDialog.INVALID_LENGTH) {
				continue;
			}
			
			Cut cut = cutTableInput.toCut();
			int quantity = cutTableInput.getQuantity();
			
			for (int i = 0; i < quantity; i++) {
				cuts.add(cut.clone());
			}
		}
		
		return new UnoptimizedCutList(barLength, cuts);
	}
	
}