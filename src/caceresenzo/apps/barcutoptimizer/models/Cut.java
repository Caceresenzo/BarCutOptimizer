package caceresenzo.apps.barcutoptimizer.models;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cut implements Cloneable, Comparable<Cut> {
	
	private final double length;
	private final int[] cutAngles;
	
	public int getLeftAngle() {
		return cutAngles[0];
	}
	
	public int getRightAngle() {
		return cutAngles[1];
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
		
		int cut1angleA = getLeftAngle();
		int cut1angleB = getRightAngle();
		int cut2angleA = other.getLeftAngle();
		int cut2angleB = other.getRightAngle();
		
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
	
	public static Cut of(double length, int leftAngle, int rightAngle) {
		return new Cut(length, new int[] { leftAngle, rightAngle });
	}
	
}