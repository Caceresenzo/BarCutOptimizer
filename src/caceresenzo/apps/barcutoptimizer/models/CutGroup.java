package caceresenzo.apps.barcutoptimizer.models;

import java.util.List;
import java.util.Objects;

/**
 * This class represent a group of cut that need to be done in a single bar.
 * 
 * @author Enzo CACERES
 */
public class CutGroup {
	
	/* Constants */
	public static final double UNKNOWN_REMAINING = -1;
	
	/* Variables */
	private final double barLength, remainingBarLength;
	private final List<Cut> cuts;
	
	/**
	 * Create a new {@link CutGroup} with a specified bar length and a {@link List} of {@link Cut}.
	 * 
	 * @param barLength
	 *            Bar length in millimeters.
	 * @param remainingBarLength
	 *            Remaining bar length after optimization in millimeters.
	 * @param cuts
	 *            {@link List} of {@link Cut} of this group.
	 * @throws IllegalStateException
	 *             If the <code>barLength</code> is <= to 0.
	 * @throws IllegalStateException
	 *             If the <code>remainingBarLength</code> is > than <code>barLength</code>.
	 * @throws NullPointerException
	 *             If the {@link List} of {@link Cut} is null.
	 * @see #validate(double, List) Input validator
	 */
	public CutGroup(double barLength, double remainingBarLength, List<Cut> cuts) {
		validate(barLength, remainingBarLength, cuts);
		
		this.barLength = barLength;
		this.remainingBarLength = remainingBarLength;
		this.cuts = cuts;
	}
	
	/** @return Bar's length in millimeters. */
	public double getBarLength() {
		return barLength;
	}
	
	/** @return Remaining <i>(supposed)</i> bar's length after optimization. */
	public double getRemainingBarLength() {
		return remainingBarLength;
	}
	
	/** @return Weather or not the {@link #getRemainingBarLength() remaining bar length} has been set to {@link #UNKNOWN_REMAINING} or not. */
	public boolean isRemainingBarLengthUnknown() {
		return remainingBarLength == UNKNOWN_REMAINING;
	}
	
	public double estimateRemainingBarLength() {
		double etimated = getBarLength();
		
		for (Cut cut : cuts) {
			etimated -= cut.getLength();
		}
		
		return etimated;
	}
	
	/** @return The {@link List} of {@link Cut} that have to be done in this group. */
	public List<Cut> getCuts() {
		return cuts;
	}
	
	public int getCutCount() {
		return cuts.size();
	}
	
	/**
	 * This function will throw exceptions if the inputs provided are not valid for future use.
	 * 
	 * @param barLength
	 *            Bar length in millimeters.
	 * @param remainingBarLength
	 *            Remaining bar length after optimization in millimeters.
	 * @param cuts
	 *            {@link List} of {@link Cut} of this group.
	 * @throws IllegalStateException
	 *             If the <code>barLength</code> is <= to 0.
	 * @throws IllegalStateException
	 *             If the <code>remainingBarLength</code> is > than <code>barLength</code>.
	 * @throws NullPointerException
	 *             If the {@link List} of {@link Cut} is null.
	 */
	public static void validate(double barLength, double remainingBarLength, List<Cut> cuts) {
		if (barLength <= 0) {
			throw new IllegalStateException("The bar length can't be under or equal to 0.");
		}
		if (remainingBarLength > barLength) {
			throw new IllegalStateException("The remaining bar length can't be bigger than the bar length.");
		}
		Objects.requireNonNull(cuts, "The list of cuts can't be null.");
	}
	
}