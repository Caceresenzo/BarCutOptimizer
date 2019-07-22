package caceresenzo.apps.barcutoptimizer.models;

import java.util.Arrays;
import java.util.Objects;

public class Cut {
	
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
	
	@Override
	public String toString() {
		return "Cut[length = " + length + ", cutAngle = " + Arrays.toString(cutAngles) + "]";
	}
}