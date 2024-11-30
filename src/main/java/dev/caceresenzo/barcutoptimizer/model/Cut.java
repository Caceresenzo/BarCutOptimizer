package dev.caceresenzo.barcutoptimizer.model;

import java.text.NumberFormat;
import java.util.OptionalDouble;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class Cut implements Comparable<Cut> {

	private final double length;
	private final double leftAngle;
	private final double rightAngle;

	@EqualsAndHashCode.Exclude
	private OptionalDouble x = OptionalDouble.empty();

	public boolean isFitting(double inLength) {
		return this.length <= inLength;
	}

	public Cut setX(double x) {
		this.x = OptionalDouble.of(x);

		return this;
	}

	public String formatLeftAngle() {
		return format(leftAngle);
	}

	public String formatRightAngle() {
		return format(rightAngle);
	}

	@Override
	@Deprecated
	public int compareTo(Cut other) {
		int result = Integer.signum((int) ((other.getLength() * 10) - (getLength() * 10)));

		if (result != 0) {
			return result;
		}

		double cut1angleA = getLeftAngle();
		double cut1angleB = getRightAngle();
		double cut2angleA = other.getLeftAngle();
		double cut2angleB = other.getRightAngle();

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

	private static String format(double x) {
		return NumberFormat.getNumberInstance().format(x);
	}

}