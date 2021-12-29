package caceresenzo.apps.barcutoptimizer.ui.components;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager.AlgorithmSettingEntry;

@SuppressWarnings("serial")
public class AlgorithmSettingPanel extends JPanel {
	
	/* Components */
	private JTextField textField;
	private JLabel actualValueLabel;
	
	/* Variables */
	private final AlgorithmSettingEntry algorithmSettingEntry;
	
	/* Swing fix... */
	private boolean textFieldForcedSetText = false;
	
	/**
	 * Create the panel.
	 */
	public AlgorithmSettingPanel(AlgorithmSettingEntry algorithmSettingEntry) {
		setSize(400, 70);
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.algorithmSettingEntry = algorithmSettingEntry;
		
		JLabel label = new JLabel(AlgorithmManager.getTranslatedName(algorithmSettingEntry));
		label.setToolTipText(AlgorithmManager.getTranslatedDescription(algorithmSettingEntry));
		
		textField = new JTextField();
		textField.setColumns(10);
		
		actualValueLabel = new JLabel("actual value");
		actualValueLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		actualValueLabel.setToolTipText((String) null);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addGap(10)
												.addComponent(textField, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(label)
												.addPreferredGap(ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
												.addComponent(actualValueLabel, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(label)
										.addComponent(actualValueLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		setLayout(groupLayout);
		
		updateUI();
		
		setMaximumSize(getSize());
		setPreferredSize(getSize());
		
		validateInput(algorithmSettingEntry.getValue());
		initializeTextFields();
	}
	
	private void initializeTextFields() {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent event) {
				// System.out.println("remove");
				sendUpdate();
			}
			
			@Override
			public void insertUpdate(DocumentEvent event) {
				// System.out.println("insert");
				sendUpdate();
			}
			
			@Override
			public void changedUpdate(DocumentEvent event) {
				// System.out.println("change");
				sendUpdate();
			}
			
			private void sendUpdate() {
				if (textFieldForcedSetText) {
					return;
				}
				
				SwingUtilities.invokeLater(() -> validateInput(textField.getText()));
			}
		});
	}
	
	private void validateInput(Object object) {
		boolean success = false;
		
		if (object instanceof String) {
			try {
				success = algorithmSettingEntry.setValueWithAutoParsing((String) object);
			} catch (Exception exception) {
				textField.setToolTipText(exception.getLocalizedMessage());
			}
		} else {
			success = algorithmSettingEntry.setValue(object);
		}
		
		if (success) {
			textField.setToolTipText("");
		}
		
		textFieldForcedSetText = true;
		textField.setText(String.valueOf(object));
		textField.setForeground(success ? Color.BLACK : Color.RED);
		textFieldForcedSetText = false;
		
		actualValueLabel.setText(String.valueOf(algorithmSettingEntry.getValue()));
	}
	
	public JLabel getActualValueLabel() {
		return actualValueLabel;
	}
	
}