package caceresenzo.apps.barcutoptimizer.models;

import java.util.Arrays;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Cut implements Cloneable, Comparable<Cut> {
	
	private final double length;
	private final int[] cutAngles;
	
	public Cut(double length, int[] cutAngles) {
		this.length = length;
		this.cutAngles = checkCutAngles(cutAngles);
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
	
	private static int[] checkCutAngles(int[] cutAngles) {
		if (cutAngles.length != 2) {
			throw new IllegalStateException("Invalid cut angles array size. (must be 2 but is " + cutAngles.length + ")");
		}
		
		return cutAngles;
	}
	
	public boolean isFitting(double inLength) {
		return this.length <= inLength;
	}
	
	@Override
	public Cut clone() {
		return new Cut(length, Arrays.copyOf(cutAngles, 2));
	}
	
	@Override
	@Deprecated
	public int compareTo(Cut other) {
		int result = Integer.signum((int) ((other.getLength() * 10) - (getLength() * 10)));
		
		if (result != 0) {
			return result;
		}
		
		int cut1angleA = getCutAngleA();
		int cut1angleB = getCutAngleB();
		int cut2angleA = other.getCutAngleA();
		int cut2angleB = other.getCutAngleB();
		
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
	}
	
	public static Cut of(double length, int angleA, int angleB) {
		return new Cut(length, new int[] { angleA, angleB });
	}
	
}