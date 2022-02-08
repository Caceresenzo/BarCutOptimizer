package caceresenzo.apps.barcutoptimizer.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;

@SuppressWarnings("serial")
public class BarCutPanel extends JPanel {
	
	private final CutGroup cutGroup;
	
	/**
	 * Create the panel.
	 */
	public BarCutPanel(CutGroup cutGroup) {
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		
		graphics2d.setColor(Color.RED);
		graphics2d.fillRect(startX, startY, maxWidth - startX, maxHeight - startY);
		
		List<Cut> cuts = cutGroup.getCuts();
		Cut lastCutElement = cuts.get(cuts.size() - 1);
		
		double x = 0;
		for (int index = 0; index < cuts.size(); index++) {
			Cut cut = cuts.get(index);
			boolean isLast = cut == lastCutElement;
			
			int x1, x2;
			
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
			
			int angleOffset = 15;
			int leftOffset = 0, rightOffset = 0;
			
			graphics2d.setColor(index % 2 == 0 ? Color.GRAY : Color.LIGHT_GRAY);
			
			if (cut.getLeftAngle() == 45) {
				leftOffset = angleOffset;
				
				graphics2d.fillPolygon(new int[] { x1, x1 + angleOffset, x1 + angleOffset }, new int[] { maxHeight, maxHeight, startY }, 3);
			}
			
			if (cut.getRightAngle() == 45) {
				rightOffset = angleOffset;
				
				graphics2d.fillPolygon(new int[] { x2, x2 - angleOffset, x2 - angleOffset }, new int[] { maxHeight, maxHeight, startY }, 3);
			}
			
			graphics2d.fillRect(x1 + leftOffset, startY, x2 - x1 - leftOffset - rightOffset, maxHeight - startY);
			
			graphics2d.setColor(Color.BLACK);
			String lengthText = String.valueOf(cut.getLength());
			Rectangle2D stringBound = graphics2d.getFontMetrics().getStringBounds(lengthText, null);
			graphics2d.drawString(lengthText, ((x2 - x1) / 2) - ((int) stringBound.getWidth() / 2) + x1, ((int) stringBound.getHeight() + maxHeight) / 2);
			
			if (!isLast) {
				int spaceBetweenElement = 8;
				x += spaceBetweenElement;
				
				graphics2d.setColor(Color.WHITE);
				graphics2d.fillRect(x2, startY, (int) (spaceBetweenElement * widthRatio), maxHeight - startY);
			}
		}
	}
	
}
