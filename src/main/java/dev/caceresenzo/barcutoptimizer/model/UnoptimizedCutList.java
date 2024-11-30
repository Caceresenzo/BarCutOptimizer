package dev.caceresenzo.barcutoptimizer.model;

import java.util.ArrayList;
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

	@Override
	public double getRemainingBarLength() {
		throw new IllegalStateException("Can't get the remaining bar length of an unoptimized cut list.");
	}

	public static UnoptimizedCutList fromCutTableInputs(List<CutTableInput> cutTableInputs, double barLength) {
		List<Cut> cuts = new ArrayList<>();

		for (CutTableInput input : cutTableInputs) {
			if (!input.hasLength()) {
				continue;
			}

			int quantity = input.getQuantity();

			for (int i = 0; i < quantity; i++) {
				cuts.add(input.toCut());
			}
		}

		return new UnoptimizedCutList(barLength, cuts);
	}

}