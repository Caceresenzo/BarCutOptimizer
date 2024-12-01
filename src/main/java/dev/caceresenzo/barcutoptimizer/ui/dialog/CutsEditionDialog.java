package dev.caceresenzo.barcutoptimizer.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.math.NumberUtils;

import dev.caceresenzo.barcutoptimizer.constant.Defaults;
import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.model.BarReference;
import dev.caceresenzo.barcutoptimizer.model.CutTableInput;
import dev.caceresenzo.barcutoptimizer.model.UnoptimizedCutList;
import dev.caceresenzo.barcutoptimizer.optimize.AlgorithmManager;
import dev.caceresenzo.barcutoptimizer.optimize.AlgorithmManager.AlgorithmSettingEntry;
import dev.caceresenzo.barcutoptimizer.optimize.CutAlgorithm;
import dev.caceresenzo.barcutoptimizer.ui.component.AlgorithmSettingPanel;

@SuppressWarnings("serial")
public class CutsEditionDialog extends JDialog {

	/* Components */
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JButton okButton;
	private JButton cancelButton;
	private JButton addLineButton;
	private JComboBox<CutAlgorithm> algorithmComboBox;
	private JLabel algorithmDescriptionLabel;
	private JPanel barLengthPanel;
	private JTextField barLengthTextField;
	private JScrollPane algorithmSettingsScrollPane;
	private JPanel algorithmSettingsListPanel;

	/* Variables */
	private final BarReference barReference;
	private final CutsEditionDialog.Callback callback;
	private double barLength;

	/**
	 * Create the dialog.
	 */
	public CutsEditionDialog(JFrame parent, BarReference barReference, CutsEditionDialog.Callback callback) {
		super(parent);

		this.barReference = barReference;
		this.callback = callback;
		this.barLength = CutTableInput.INVALID_LENGTH;

		if (parent != null) {
			setModal(true);
		}

		setSize(900, 600);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setResizable(true);
		setLocationRelativeTo(null);
		setTitle(I18n.string("cut-editor.frame.title"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setBorder(new TitledBorder(null, I18n.string("cut-editor.panel.data"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setBorder(new TitledBorder(null, I18n.string("cut-editor.panel.algorithm"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		barLengthPanel = new JPanel();
		barLengthPanel.setBorder(new TitledBorder(null, I18n.string("cut-editor.panel.bar-length"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		algorithmSettingsScrollPane = new JScrollPane();
		algorithmSettingsScrollPane.setBorder(new TitledBorder(null, I18n.string("cut-editor.panel.algorithm-settings"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(barLengthPanel, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
						.addComponent(algorithmSettingsScrollPane, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
						.addComponent(algorithmPanel, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(2)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(algorithmPanel, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
							.addGap(8)
							.addComponent(algorithmSettingsScrollPane, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(barLengthPanel, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);

		algorithmSettingsListPanel = new JPanel();
		algorithmSettingsScrollPane.setViewportView(algorithmSettingsListPanel);
		algorithmSettingsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		algorithmSettingsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		algorithmSettingsListPanel.setLayout(new BoxLayout(algorithmSettingsListPanel, BoxLayout.Y_AXIS));

		barLengthTextField = new JTextField();
		barLengthTextField.setColumns(10);
		GroupLayout gl_barLengthPanel = new GroupLayout(barLengthPanel);
		gl_barLengthPanel.setHorizontalGroup(
			gl_barLengthPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_barLengthPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(barLengthTextField, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
					.addContainerGap()));
		gl_barLengthPanel.setVerticalGroup(
			gl_barLengthPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_barLengthPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(barLengthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(65, Short.MAX_VALUE)));
		barLengthPanel.setLayout(gl_barLengthPanel);

		algorithmComboBox = new JComboBox<CutAlgorithm>();

		algorithmDescriptionLabel = new JLabel("DESCRIPTION");
		algorithmDescriptionLabel.setToolTipText("");
		GroupLayout gl_algorithmPanel = new GroupLayout(algorithmPanel);
		gl_algorithmPanel.setHorizontalGroup(
			gl_algorithmPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_algorithmPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_algorithmPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(algorithmDescriptionLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
						.addComponent(algorithmComboBox, Alignment.TRAILING, 0, 349, Short.MAX_VALUE))
					.addContainerGap()));
		gl_algorithmPanel.setVerticalGroup(
			gl_algorithmPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_algorithmPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(algorithmComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(algorithmDescriptionLabel, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		algorithmPanel.setLayout(gl_algorithmPanel);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new CutTableInputTableModel(CutTableInput.createListFromBarReference(barReference)));
		table.setBackground(SystemColor.menu);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		tableScrollPane.setViewportView(table);
		contentPanel.setLayout(gl_contentPanel);

		addLineButton = new JButton(I18n.string("cut-editor.button.add-new-line"));
		addLineButton.setActionCommand("");
		okButton = new JButton(I18n.string("cut-editor.button.ok"));
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton(I18n.string("cut-editor.button.cancel"));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(addLineButton, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
						.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, 509, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addComponent(addLineButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);

		initializeButtons();
		initializeComboBoxes();
		initializeTextFields();

		for (Class<?> clazz : Arrays.asList(Object.class, Number.class)) {
			DefaultCellEditor cellEditor = (DefaultCellEditor) table.getDefaultEditor(clazz);

			JTextField textField = (JTextField) cellEditor.getComponent();

			textField.addFocusListener(new FocusAdapter() {

				@Override
				public void focusGained(FocusEvent event) {
					textField.selectAll();
				}

			});
		}

		table.addPropertyChangeListener("tableCellEditor", (event) -> {
			DefaultCellEditor cellEditor = (DefaultCellEditor) table.getCellEditor();

			if (cellEditor != null) {
				Component component = cellEditor.getComponent();

				if (component instanceof JTextField) {
					((JTextField) component).selectAll();
				}
			}
		});

		pack();

		SwingUtilities.invokeLater(() -> {
			if (barReference.isEmpty()) {
				newItem();
			}
		});
	}

	private void initializeButtons() {
		addLineButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				newItem();
			}

		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				callback.onCancel(barReference);
				setVisible(false);
			}

		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					UnoptimizedCutList unoptimizedCutList = UnoptimizedCutList.fromCutTableInputs(((CutTableInputTableModel) table.getModel()).getRows(), barLength);

					barReference.optimize(unoptimizedCutList, (CutAlgorithm) algorithmComboBox.getSelectedItem());

					callback.onFinish(barReference);
				} catch (Exception exception) {
					exception.printStackTrace();

					JOptionPane.showConfirmDialog(CutsEditionDialog.this, I18n.string("cut-algorithm.error.failed", exception.getLocalizedMessage()), I18n.string("dialog.error.title"), JOptionPane.ERROR_MESSAGE);

					callback.onException(barReference, exception);
				}

				setVisible(false);
			}

		});
	}

	private void initializeComboBoxes() {
		algorithmComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(AlgorithmManager.get().getCutAlgorithms())));

		algorithmComboBox.setRenderer(new BasicComboBoxRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				CutAlgorithm cutAlgorithm = (CutAlgorithm) value;

				setText(AlgorithmManager.getTranslatedName(cutAlgorithm));

				return this;
			}

		});

		algorithmComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				CutAlgorithm selectedAlgorithm = (CutAlgorithm) algorithmComboBox.getSelectedItem();

				if (selectedAlgorithm != null) {
					onAlgorithmSelected(selectedAlgorithm);
				}
			}

		});

		algorithmComboBox.setSelectedIndex(0);
	}

	private void initializeTextFields() {
		double initialValue = Defaults.COMMON_BAR_LENGTH;

		if (!barReference.getCutGroups().isEmpty()) {
			initialValue = barReference.getCutGroups().get(0).getBarLength();
		}

		barLengthTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent event) {
				sendUpdate();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				sendUpdate();
			}

			@Override
			public void changedUpdate(DocumentEvent event) {
				sendUpdate();
			}

			private void sendUpdate() {
				SwingUtilities.invokeLater(() -> updateBarLength(barLengthTextField.getText()));
			}

		});

		barLengthTextField.setText(String.valueOf(initialValue));
	}

	private void newItem() {
		((CutTableInputTableModel) table.getModel()).addNewEmptyItem();
		table.repaint();
	}

	private void onAlgorithmSelected(CutAlgorithm cutAlgorithm) {
		algorithmDescriptionLabel.setText(AlgorithmManager.getTranslatedDescription(cutAlgorithm));

		algorithmSettingsListPanel.removeAll();
		algorithmSettingsListPanel.revalidate();
		algorithmSettingsListPanel.repaint();

		List<AlgorithmSettingEntry> settingEntries = AlgorithmManager.get().getAlgorithmSettingEntries(cutAlgorithm);
		for (AlgorithmSettingEntry entry : settingEntries) {
			algorithmSettingsListPanel.add(new AlgorithmSettingPanel(entry));
		}
	}

	private void updateBarLength(String text) {
		double value = NumberUtils.toDouble(text, CutTableInput.INVALID_LENGTH);

		if (value < 0) {
			barLengthTextField.setForeground(Color.RED);
			return;
		}

		barLengthTextField.setForeground(Color.BLACK);
		barLength = value;
	}

	public static CutsEditionDialog open(JFrame parent, BarReference barReference, CutsEditionDialog.Callback callback) {
		CutsEditionDialog dialog = new CutsEditionDialog(parent, barReference, callback);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		return dialog;
	}

	public static interface Callback {

		void onFinish(BarReference barReference);

		void onCancel(BarReference barReference);

		void onException(BarReference barReference, Exception exception);

	}

	public class CutTableInputTableModel extends AbstractTableModel {

		private final List<CutTableInput> rows;

		private final String[] columnNames;
		private final Class<?>[] columnClass;

		public CutTableInputTableModel(List<CutTableInput> cutInputs) {
			this.columnNames = new String[] {
				I18n.string("cut-editor.panel.data.table.header.column.length"),
				I18n.string("cut-editor.panel.data.table.header.column.angle-a"),
				I18n.string("cut-editor.panel.data.table.header.column.angle-b"),
				I18n.string("cut-editor.panel.data.table.header.column.quantity"),
				I18n.string("cut-editor.panel.data.table.header.column.remove"),
			};

			this.columnClass = new Class[] {
				Double.class,
				Double.class,
				Double.class,
				Integer.class,
				Boolean.class
			};

			this.rows = new ArrayList<>(cutInputs);
		}

		public void addNewEmptyItem() {
			rows.add(new CutTableInput());
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClass[columnIndex];
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CutTableInput row = rows.get(rowIndex);

			switch (columnIndex) {
				case 0: {
					double length = row.getLength();

					if (length < 1) {
						return null;
					}

					return length;
				}

				case 1: {
					return row.getLeftAngle();
				}

				case 2: {
					return row.getRightAngle();
				}

				case 3: {
					return row.getQuantity();
				}

				case 4: {
					return false;
				}

				default: {
					break;
				}
			}

			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			CutTableInput row = rows.get(rowIndex);

			switch (columnIndex) {
				case 0: {
					row.setLength((Double) aValue);
					break;
				}

				case 1: {
					row.setLeftAngle((Double) aValue);
					break;
				}

				case 2: {
					row.setRightAngle((Double) aValue);
					break;
				}

				case 3: {
					row.setQuantity((Integer) aValue);
					break;
				}

				case 4: {
					rows.remove(row);
					table.repaint();
					break;
				}

				default: {
					break;
				}
			}
		}

		public List<CutTableInput> getRows() {
			return rows;
		}

	}

}