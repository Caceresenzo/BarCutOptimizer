package dev.caceresenzo.barcutoptimizer.ui.components;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.models.BarReference;

@SuppressWarnings("serial")
public class BarReferenceInfoPanel extends JPanel {
	
	/* Components */
	private JTextField consumedBarTextField;
	private JLabel consumedBarLabel;
	private JTextField totalCutsTextField;
	
	/* Variables */
	private final BarReference barReference;
	
	/**
	 * Create the panel.
	 */
	public BarReferenceInfoPanel(BarReference barReference) {
		this.barReference = barReference;
		
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), I18n.string("editor.list.item.bar-reference-information.title"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setSize(300, 90);
		// setMaximumSize(getSize());
		// setPreferredSize(getSize());
		setBackground(UIManager.getColor("Tree.textBackground"));
		
		consumedBarLabel = new JLabel(I18n.string("editor.list.item.bar-reference-information.item.consumed-bar-count"));
		
		consumedBarTextField = new JTextField();
		consumedBarTextField.setEditable(false);
		consumedBarTextField.setColumns(10);
		
		totalCutsTextField = new JTextField();
		totalCutsTextField.setEditable(false);
		totalCutsTextField.setColumns(10);
		
		JLabel totalCutsLabel = new JLabel(I18n.string("editor.list.item.bar-reference-information.item.total-cuts-count"));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(totalCutsLabel, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
										.addComponent(consumedBarLabel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 197, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(totalCutsTextField, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
										.addComponent(consumedBarTextField, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(consumedBarLabel, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
										.addComponent(consumedBarTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(totalCutsLabel, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
										.addComponent(totalCutsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(20, Short.MAX_VALUE)));
		setLayout(groupLayout);
		
		showInformations();
	}
	
	private void showInformations() {
		consumedBarTextField.setText(String.valueOf(barReference.getCutGroups().size()));
		totalCutsTextField.setText(String.valueOf(barReference.getAllCuts().size()));
	}
	
}