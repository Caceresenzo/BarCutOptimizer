package dev.caceresenzo.barcutoptimizer.ui.component;

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
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dev.caceresenzo.barcutoptimizer.optimize.AlgorithmManager;
import dev.caceresenzo.barcutoptimizer.optimize.AlgorithmManager.AlgorithmSettingEntry;

@SuppressWarnings("serial")
public class AlgorithmSettingPanel extends JPanel {

	/* Components */
	private JTextField textField;
	private JLabel actualValueLabel;

	/* Variables */
	private final AlgorithmSettingEntry algorithmSettingEntry;

	/**
	 * Create the panel.
	 */
	public AlgorithmSettingPanel(AlgorithmSettingEntry algorithmSettingEntry) {
		setSize(269, 62);
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), AlgorithmManager.getTranslatedName(algorithmSettingEntry), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.algorithmSettingEntry = algorithmSettingEntry;

		JLabel label = new JLabel(AlgorithmManager.getTranslatedDescription(algorithmSettingEntry));

		textField = new JTextField();
		textField.setText(String.valueOf(algorithmSettingEntry.getValue()));

		actualValueLabel = new JLabel("actual value");
		actualValueLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(label, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(actualValueLabel, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(actualValueLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

		updateUI();

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

		textField.setForeground(success ? Color.BLACK : Color.RED);

		actualValueLabel.setText(String.valueOf(algorithmSettingEntry.getValue()));
	}

	public JLabel getActualValueLabel() {
		return actualValueLabel;
	}

}