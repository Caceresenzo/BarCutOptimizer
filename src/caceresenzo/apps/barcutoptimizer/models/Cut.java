package caceresenzo.apps.barcutoptimizer.models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Cut implements Cloneable {
	
	private final double length;
	private final int[] cutAngles;
	private int atLineIndex;
	
	private Cut(double length, int[] cutAngles) {
		this.length = length;
		this.cutAngles = checkCutAngles(cutAngles);
	}
	
	public double getLength() {
		return length;
	}
	
	public int[] getCutAngles() {
		return cutAngles;
	}
	
	public int getCutAngleA() {
		return cutAngles[0];
	}
	
	public int getCutAngleB() {
		return cutAngles[1];
	}
	
	public boolean isAngleDegree(int angle, int degree) {
		if (angle < 0 || angle > 1) {
			throw new IllegalArgumentException("The angle parameter must be eather 0 or 1.");
		}
		
		return getCutAngles()[angle] == degree;
	}
	
	public boolean hasSameCutProperties(Cut other) {
		return length == other.length && cutAngles[0] == other.cutAngles[0] && cutAngles[1] == other.cutAngles[1];
	}
	
	private static int[] checkCutAngles(int[] cutAngles) {
		if (cutAngles.length != 2) {
			throw new IllegalStateException("Invalid cut angles array size. (must be 2 but is " + cutAngles.length + ")");
		}
		
		return cutAngles;
	}
	
	public Cut atLine(int index) {
		this.atLineIndex = index;
		
		return this;
	}
	
	public int getAtLineIndex() {
		return atLineIndex;
	}
	
	public boolean isFitting(double inLength) {
		return this.length <= inLength;
	}
	
	@Override
	public String toString() {
		return "Cut[length = " + length + ", cutAngle = " + Arrays.toString(cutAngles) + "]";
	}
	
	public CutTableInput toTableInput() {
		CutTableInput cutTableInput = new CutTableInput();
		
		cutTableInput.setLength(getLength());
		cutTableInput.setCutAngles(getCutAngles());
		
		return cutTableInput;
	}
	
	@Override
	public Cut clone() {
		Cut cut = new Cut(getLength(), getCutAngles());
		cut.atLineIndex = getAtLineIndex();
		
		return cut;
	}
	
	public static Cut fromExtractedLine(String line) {
		// System.out.println(line);
		String[] rawData = Objects.requireNonNull(line.split(" "), "Can't construct a Cut object with a null extracted line.");
		
		int firstDegreeCharOffset = 0;
		for (int index = 0; index < rawData.length; index++) {
			String part = rawData[index];
			
			if (part.toCharArray()[0] == '°') {
				firstDegreeCharOffset = index;
				break;
			}
		}
		
		int zero = firstDegreeCharOffset - 2;
		
		if (zero < 0) {
			zero = 0;
		}
		
		double length = Double.valueOf(rawData[zero]);
		int angleA = (int) (double) Double.valueOf(rawData[zero + 1]);
		int angleB = (int) (double) Double.valueOf(rawData[zero + 3]);
		
		return new Cut(length, new int[] { angleA, angleB });
	}
	
	public static Cut dummy(double length, int angleA, int angleB) {
		return new Cut(length, new int[] { angleA, angleB });
	}
	
	public static void sortByLengthAndAngles(List<Cut> cuts) {
		cuts.sort(new Comparator<Cut>() {
			@Override
			public int compare(Cut cut1, Cut cut2) {
				int result = Integer.signum((int) ((cut2.getLength() * 10) - (cut1.getLength() * 10)));
				
				if (result != 0) {
					return result;
				}
				
				int cut1angleA = cut1.getCutAngleA();
				int cut1angleB = cut1.getCutAngleB();
				int cut2angleA = cut2.getCutAngleA();
				int cut2angleB = cut2.getCutAngleB();
				
				if ((cut1angleA == 90 && cut1angleB == 90) && (cut2angleA == 90 && cut2angleB == 90)) {
					return 0;
				}
				
				if ((cut1angleA == 45 || cut1angleB == 45) && (cut2angleA == 90 && cut2angleB == 90)) {
					return 1;
				}
				
				if ((cut1angleA == 90 && cut1angleB == 90) && (cut2angleA == 45 || cut2angleB == 45)) {
					return -1;
				}
				
				if ((cut1angleA == 90 || cut1angleB == 90) && (cut2angleA == 45 && cut2angleB == 45)) {
					return -1;
				}
				
				if ((cut1angleA == 45 && cut1angleB == 45) && (cut2angleA == 90 || cut2angleB == 90)) {
					return 1;
				}
				
				if ((cut1angleA == 90 && cut1angleB == 45) && (cut2angleA == 45 && cut2angleB == 90)) {
					return -1;
				}
				
				if ((cut1angleA == 45 && cut1angleB == 90) && (cut2angleA == 90 && cut2angleB == 45)) {
					return 1;
				}
				
				return 0;
				
				/**
				 * 
				 * Your comparison inside the sort will: A,B,C are in highest to lowest prioerity
				 * 
				 * <pre>
				 * Compare Element 1's A with Element 2's A
				 * 		If greater or less return result
				 * 		Else Compare B
				 * 			If greater or less return result
				 * 			Else Compare C return result
				 * </pre>
				 * 
				 * This can be extrapolated to A..n criteria with a simple loop.
				 * 
				 * <pre>
				 * For Each Criteria in list of Criteria
				 * 		Compare Element 1's Criteria with Element 2's
				 * 			If greater or less return result
				 * 			Else continue // for clarity
				 * Return equal
				 * </pre>
				 * 
				 * The above both assume your Comparison function is Compare ( Element1, Element2 )
				 * 
				 * @thanks https://stackoverflow.com/a/7431831/7292958
				 */
				
				// int resultCutAngleA = cut2angleA - cut1angleA;
				// int resultCutAngleB = cut2angleB - cut1angleB;
				//
				// System.out.println(String.format("1: %s/%s, 2: %s/%s -> result %s %s", cut1angleA, cut1angleB, cut2angleA, cut2angleB, resultCutAngleA, resultCutAngleB));
				//
				// if (resultCutAngleA != 0) {
				// return resultCutAngleB;
				// }
				//
				// return 0;
			}
		});
	}
	
	public static void main(String[] args) {
		Cut a = Cut.dummy(0, 90, 90).atLine(0);
		Cut b = Cut.dummy(0, 45, 90).atLine(1);
		Cut c = Cut.dummy(0, 45, 45).atLine(2);
		Cut d = Cut.dummy(0, 90, 45).atLine(3);
		
		List<Cut> cuts = Arrays.asList(a, b, c, d, a, b, c, d);
		Cut.sortByLengthAndAngles(cuts);
		
		for (Cut cut : cuts) {
			System.out.println(cut.toString() + " [" + cut.getAtLineIndex() + "]");
		}
	}
	
	public static Cut fromCutTableInput(CutTableInput cutTableInput) {
		return new Cut(cutTableInput.getLength(), cutTableInput.getCutAngles());
	}
}