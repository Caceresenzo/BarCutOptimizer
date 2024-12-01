package dev.caceresenzo.barcutoptimizer.ui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import dev.caceresenzo.barcutoptimizer.model.Cut;
import dev.caceresenzo.barcutoptimizer.model.CutGroup;

@SuppressWarnings("serial")
public class BarCutPanel extends JPanel {

	public static final Color RED = Color.decode("#ff4300");
	public static final Color BLUE = Color.decode("#00f3ff");
	public static final Color GREEN = Color.decode("#00ff42");

	private final CutGroup cutGroup;

	/**
	 * Create the panel.
	 */
	public BarCutPanel(CutGroup cutGroup) {
		setBorder(new LineBorder(new Color(255, 255, 255)));
		this.cutGroup = cutGroup;
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		if (cutGroup == null) {
			return;
		}

		Graphics2D graphics2d = (Graphics2D) graphics;

		int startX = 2;
		int startY = 2;
		int maxWidth = getWidth() - startX;
		int maxHeight = getHeight() - startY;

		double widthRatio = maxWidth / cutGroup.getBarLength();

		graphics2d.setColor(RED);
		graphics2d.fillRect(startX, startY, maxWidth - startX, maxHeight - startY);

		List<Cut> cuts = cutGroup.getCuts();
		Cut lastCutElement = cuts.get(cuts.size() - 1);

		double x = 0;
		for (int index = 0; index < cuts.size(); index++) {
			Cut cut = cuts.get(index);
			boolean isLast = cut == lastCutElement;

			int x1;
			int x2;

			// TODO Will cause instability if one has a X value and the next don't
			if (cut.getX().isPresent()) {
				x = cut.getX().getAsDouble();
				x1 = (int) (x * widthRatio);
				x2 = (int) ((x + cut.getLength()) * widthRatio);
			} else {
				x1 = (int) (x * widthRatio) + startX;
				x += cut.getLength();
				x2 = (int) (x * widthRatio) + startX;
			}

			int height = maxHeight - startY;
			int width = x2 - x1;

			graphics2d.setColor(index % 2 == 0 ? BLUE : GREEN);

			Slope leftSlope = new Slope(cut.getLeftAngle(), startY, maxHeight, width, height);
			if (leftSlope.yPoints != null) {
				int[] xPoints = {
					/* bottom left  */ x1,
					/* bottom right */ x1 + leftSlope.offset,
					/* top right    */ x1 + leftSlope.offset
				};

				graphics2d.fillPolygon(xPoints, leftSlope.yPoints, 3);
			}

			Slope rightSlope = new Slope(cut.getRightAngle(), startY, maxHeight, width, height);
			if (rightSlope.yPoints != null) {
				int[] xPoints = {
					/* bottom right */ x2,
					/* bottom left  */ x2 - rightSlope.offset,
					/* top left     */ x2 - rightSlope.offset
				};

				graphics2d.fillPolygon(xPoints, rightSlope.yPoints, 3);
			}

			graphics2d.fillRect(
				x1 + leftSlope.offset,
				startY,
				width - leftSlope.offset - rightSlope.offset,
				height
			);

			graphics2d.setColor(Color.BLACK);
			String lengthText = String.valueOf(cut.getLength());
			Rectangle2D stringBound = graphics2d.getFontMetrics().getStringBounds(lengthText, null);
			graphics2d.drawString(
				lengthText,
				((width) / 2) - ((int) stringBound.getWidth() / 2) + x1,
				((int) stringBound.getHeight() + maxHeight) / 2 - 1
			);

			if (!isLast) {
				int spaceBetweenElement = 8;
				x += spaceBetweenElement;

				graphics2d.setColor(Color.WHITE);
				graphics2d.fillRect(
					x2,
					startY,
					Math.max(
						(int) (spaceBetweenElement * widthRatio),
						1
					),
					maxHeight - startY
				);
			}
		}
	}

	class Slope {

		int[] yPoints;
		int offset;

		public Slope(
			double angle,
			final int yStart,
			final int yEnd,
			final int barWidth,
			final int barHeight
		) {
			offset = 0;

			if (angle == 90) {
				return;
			}

			int top = yStart;
			int bottom = yEnd;
			boolean inverted = false;

			if (angle > 90) {
				top = yEnd;
				bottom = yStart;
				inverted = true;

				angle = 180 - angle;
			}

			offset = (int) (yEnd / Math.tan(Math.toRadians(angle)));

			final int halfBarWidth = barWidth / 2;
			if (offset > halfBarWidth) {
				int newHeight = Math.min(
					(int) (Math.tan(Math.toRadians(angle)) * halfBarWidth),
					barHeight
				);

				if (inverted) {
					top = newHeight;
				} else {
					top = bottom - newHeight;
				}

				offset = halfBarWidth;
			}

			yPoints = new int[] {
				bottom,
				bottom,
				top
			};
		}

	}

}