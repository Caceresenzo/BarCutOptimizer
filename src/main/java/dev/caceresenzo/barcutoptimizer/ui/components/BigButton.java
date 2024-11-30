package dev.caceresenzo.barcutoptimizer.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class BigButton extends JPanel {
	
	/** Create the panel. */
	public BigButton(String title, String iconClasspathUrl, OnBigButtonClickListener bigButtonClickListener) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setSize(200, 240);
		
		JLabel iconLabel = new JLabel("");
		iconLabel.setSize(150, 150);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setIcon(new ImageIcon(BigButton.class.getResource(iconClasspathUrl)));
		
		JLabel titleLabel = new JLabel("<html><center>" + title.replace("\n", "<br/>") + "</center></html>");
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | Font.BOLD, titleLabel.getFont().getSize() + 10f));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(titleLabel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 178, Short.MAX_VALUE)
										.addComponent(iconLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(iconLabel, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
								.addContainerGap()));
		setLayout(groupLayout);
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {
				setBackground(UIManager.getColor("Button.background"));
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				setBackground(UIManager.getColor("Button.highlight"));
			}
			
			@Override
			public void mouseExited(MouseEvent event) {
				setBackground(UIManager.getColor("Button.background"));
			}
			
			@Override
			public void mouseEntered(MouseEvent event) {
				setBackground(UIManager.getColor("Button.light"));
			}
			
			@Override
			public void mouseClicked(MouseEvent event) {
				if (bigButtonClickListener != null) {
					bigButtonClickListener.onClick((BigButton) event.getComponent());
				}
			}
		});
	}
	
	/** Simple listener class used when a {@link MouseEvent mouse clicked event} is detected on a {@link BigButton}. */
	public static interface OnBigButtonClickListener {
		
		public void onClick(BigButton bigButton);
		
	}
	
}
