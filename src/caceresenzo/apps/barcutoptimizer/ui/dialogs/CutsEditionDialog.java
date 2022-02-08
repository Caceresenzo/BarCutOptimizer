package caceresenzo.apps.barcutoptimizer.ui.dialogs;

import java.awt.BorderLayout;
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

import caceresenzo.apps.barcutoptimizer.config.Constants;
import caceresenzo.apps.barcutoptimizer.config.I18n;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager.AlgorithmSettingEntry;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.CutAlgorithm;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.models.CutTableInput;
import caceresenzo.apps.barcutoptimizer.models.UnoptimizedCutList;
import caceresenzo.apps.barcutoptimizer.ui.components.AlgorithmSettingPanel;

@SuppressWarnings("serial")
public class CutsEditionDialog extends JDialog implements Constants {
	
	/* Constants */
	public static final double INVALID_LENGTH = -1;
	
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
		this.barLength = INVALID_LENGTH;
		
		if (parent != null) {
			setModal(true);
		}
		
		setSize(900, 600);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setResizable(true);
		setLocationRelativeTo(null);
		setTitle(I18n.string("cut-editor.frame.title"));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
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
								.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(algorithmSettingsScrollPane, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
										.addComponent(barLengthPanel, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
										.addComponent(algorithmPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		gl_contentPanel.setVerticalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
								.addGap(2)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
										.addGroup(gl_contentPanel.createSequentialGroup()
												.addComponent(algorithmPanel, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
												.addGap(8)
												.addComponent(algorithmSettingsScrollPane, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(barLengthPanel, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)))));
		
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
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton(I18n.string("cut-editor.button.ok"));
		cancelButton = new JButton(I18n.string("cut-editor.button.cancel"));
		getRootPane().setDefaultButton(okButton);
		
		addLineButton = new JButton(I18n.string("cut-editor.button.add-new-line"));
		addLineButton.setActionCommand("");
		GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
		gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_buttonPane.createSequentialGroup()
								.addContainerGap()
								.addComponent(addLineButton, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, 291, Short.MAX_VALUE)
								.addComponent(okButton)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(cancelButton)
								.addContainerGap()));
		gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_buttonPane.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(cancelButton)
										.addComponent(okButton)
										.addComponent(addLineButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		buttonPane.setLayout(gl_buttonPane);
		
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
	}
	
	private void initializeButtons() {
		addLineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				((CutTableInputTableModel) table.getModel()).addNewEmptyItem();
				table.repaint();
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
					UnoptimizedCutList unoptimizedCutList = UnoptimizedCutList.fromCutTableInputs(((CutTableInputTableModel) table.getModel()).getCutTableInputs(), barLength);
					
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
		double initialValue = COMMON_BAR_LENGTH;
		
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
		double value = NumberUtils.toDouble(text, INVALID_LENGTH);
		
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
		private final List<CutTableInput> cutTableInputs;
		
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
			this.columnClass = new Class[] { Double.class, Integer.class, Integer.class, Integer.class, Boolean.class };
			
			this.cutTableInputs = new ArrayList<>(cutInputs);
		}
		
		public void addNewEmptyItem() {
			cutTableInputs.add(new CutTableInput());
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
			return cutTableInputs.size();
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CutTableInput row = cutTableInputs.get(rowIndex);
			
			switch (columnIndex) {
				case 0: {
					double length = row.getLength();
					
					if (length < 1) {
						return null;
					}
					
					return length;
				}
				
				case 1: {
					return row.getCutAngles()[0];
				}
				
				case 2: {
					return row.getCutAngles()[1];
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
			CutTableInput row = cutTableInputs.get(rowIndex);
			
			switch (columnIndex) {
				case 0: {
					row.setLength((Double) aValue);
					break;
				}
				
				case 1: {
					row.getCutAngles()[0] = (Integer) aValue;
					break;
				}
				
				case 2: {
					row.getCutAngles()[1] = (Integer) aValue;
					break;
				}
				
				case 3: {
					row.setQuantity((Integer) aValue);
					break;
				}
				
				case 4: {
					cutTableInputs.remove(row);
					table.repaint();
					break;
				}
				
				default: {
					break;
				}
			}
		}
		
		public List<CutTableInput> getCutTableInputs() {
			return cutTableInputs;
		}
	}
	
}