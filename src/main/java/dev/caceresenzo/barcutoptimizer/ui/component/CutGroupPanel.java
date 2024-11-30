package dev.caceresenzo.barcutoptimizer.ui.component;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.model.CutGroup;

@SuppressWarnings("serial")
public class CutGroupPanel extends JPanel {
	
	/* Variables */
	private final CutGroup cutGroup;
	
	/** Create the panel. */
	public CutGroupPanel(CutGroup cutGroup) {
		this.cutGroup = cutGroup;
		
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setSize(250, 110);
		setBackground(UIManager.getColor("Tree.textBackground"));
		
		JLabel cutGroupTitleLabel = new JLabel(I18n.string("editor.list.item.cut-group.title.format" + (cutGroup.isRemainingBarLengthUnknown() ? ".without-remaining" : ""), cutGroup.getBarLength(), cutGroup.getCutCount(), cutGroup.getRemainingBarLength()));
		cutGroupTitleLabel.setFont(cutGroupTitleLabel.getFont().deriveFont(cutGroupTitleLabel.getFont().getSize() + 8f));
		
		JLabel cutListLabel = new JLabel("");
		cutListLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		JPanel barCutPanel = new BarCutPanel(cutGroup);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(cutListLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
										.addComponent(cutGroupTitleLabel)
										.addComponent(barCutPanel, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(cutGroupTitleLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(barCutPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(cutListLabel, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
								.addContainerGap()));
		GroupLayout gl_barCutPanel = new GroupLayout(barCutPanel);
		gl_barCutPanel.setHorizontalGroup(
				gl_barCutPanel.createParallelGroup(Alignment.LEADING)
						.addGap(0, 10, Short.MAX_VALUE));
		gl_barCutPanel.setVerticalGroup(
				gl_barCutPanel.createParallelGroup(Alignment.LEADING)
						.addGap(0, 10, Short.MAX_VALUE));
		barCutPanel.setLayout(gl_barCutPanel);
		setLayout(groupLayout);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		String format = "%-10s %7s / %-5s <br/>";
		stringBuilder.append(String.format(format, "LENGTH", "ANGLE A", "ANGLE B"));
		cutGroup.getCuts().forEach((cut) -> stringBuilder.append(String.format(format, cut.getLength(), cut.getCutAngles()[0], cut.getCutAngles()[1])));
		stringBuilder.append("</html>");
		
		cutListLabel.setText(stringBuilder.toString().replace(" ", "&nbsp;"));
	}
	
	public CutGroup getCutGroup() {
		return cutGroup;
	}
	
}