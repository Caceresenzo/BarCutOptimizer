package dev.caceresenzo.barcutoptimizer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CutTableInput {

	public static final double INVALID_LENGTH = -1;
	public static final double ANGLE_MINIMUM = 1;
	public static final double ANGLE_MAXIMUM = 179;

	private double length = INVALID_LENGTH;
	private double leftAngle = 90.0;
	private double rightAngle = 90.0;
	private int quantity = 1;

	public CutTableInput(double length, double leftAngle, double rightAngle, int quantity) {
		this.length = length;
		setLeftAngle(leftAngle);
		setRightAngle(rightAngle);
		this.quantity = quantity;
	}

	public void setLeftAngle(double leftAngle) {
		this.leftAngle = safeAngle(leftAngle);
	}

	public void setRightAngle(double rightAngle) {
		this.rightAngle = safeAngle(rightAngle);
	}

	public boolean hasLength() {
		return length != INVALID_LENGTH;
	}

	public Cut toCut() {
		return new Cut(length, leftAngle, rightAngle);
	}

	public static CutTableInput fromCut(Cut cut, int quantity) {
		return new CutTableInput(
			cut.getLength(),
			cut.getLeftAngle(),
			cut.getRightAngle(),
			quantity
		);
	}

	public static List<CutTableInput> createListFromBarReference(BarReference barReference) {
		List<CutTableInput> list = new ArrayList<>();
		Map<Cut, Integer> countMap = barReference.computeCutCountMap();

		for (Entry<Cut, Integer> entry : countMap.entrySet()) {
			Cut cut = entry.getKey();
			int quantity = entry.getValue();

			list.add(fromCut(cut, quantity));
		}

		return list;
	}

	private static double safeAngle(double x) {
		return Math.clamp(
			Math.round(x * 100d) / 100d,
			ANGLE_MINIMUM,
			ANGLE_MAXIMUM
		);
	}

}