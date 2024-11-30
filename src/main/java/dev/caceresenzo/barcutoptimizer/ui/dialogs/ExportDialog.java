package dev.caceresenzo.barcutoptimizer.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import dev.caceresenzo.barcutoptimizer.config.Constants;
import dev.caceresenzo.barcutoptimizer.config.I18n;
import dev.caceresenzo.barcutoptimizer.logic.exporter.ExporterCallback;
import dev.caceresenzo.barcutoptimizer.logic.exporter.implementations.PdfDataExporter;
import dev.caceresenzo.barcutoptimizer.models.BarReference;
import dev.caceresenzo.barcutoptimizer.ui.BarCutOptimizerWindow;

@SuppressWarnings("serial")
public class ExportDialog extends JDialog implements Constants {
	
	/* Components */
	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	private JProgressBar etaProgressBar;
	private JProgressBar progressProgressBar;
	private JLabel etaLabel;
	private JButton selectFileButton;
	private JButton closeButton;
	private JButton exportButton;
	
	/* Variables */
	private final List<BarReference> barReferences;
	
	/* Export */
	private File targetFile;
	
	/**
	 * Create the dialog.
	 */
	public ExportDialog(JFrame parent, List<BarReference> barReferences) {
		super(parent);
		
		this.barReferences = barReferences;
		
		setSize(450, 300);
		setTitle(I18n.string("export.frame.title"));
		getContentPane().setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setModal(true);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel filePanel = new JPanel();
		filePanel.setBorder(new TitledBorder(null, I18n.string("export.panel.destination"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel progressPanel = new JPanel();
		progressPanel.setBorder(new TitledBorder(null, I18n.string("export.panel.progress"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(filePanel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.addComponent(progressPanel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE));
		gl_contentPanel.setVerticalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(filePanel, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(progressPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(29, Short.MAX_VALUE)));
		
		etaLabel = new JLabel(I18n.string("export.eta.not-even-started"));
		etaLabel.setFont(etaLabel.getFont().deriveFont(etaLabel.getFont().getSize() + 8f));
		etaLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		etaProgressBar = new JProgressBar();
		
		progressProgressBar = new JProgressBar();
		GroupLayout gl_progressPanel = new GroupLayout(progressPanel);
		gl_progressPanel.setHorizontalGroup(
				gl_progressPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_progressPanel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_progressPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(etaLabel, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
										.addComponent(progressProgressBar, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
										.addComponent(etaProgressBar, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
								.addContainerGap()));
		gl_progressPanel.setVerticalGroup(
				gl_progressPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_progressPanel.createSequentialGroup()
								.addComponent(etaLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(etaProgressBar, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addGap(11)
								.addComponent(progressProgressBar, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		progressPanel.setLayout(gl_progressPanel);
		
		filePathTextField = new JTextField();
		filePathTextField.setEditable(false);
		filePathTextField.setColumns(10);
		
		selectFileButton = new JButton(I18n.string("export.button.select-file"));
		GroupLayout gl_filePanel = new GroupLayout(filePanel);
		gl_filePanel.setHorizontalGroup(
				gl_filePanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_filePanel.createSequentialGroup()
								.addContainerGap()
								.addComponent(selectFileButton)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(filePathTextField, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
								.addContainerGap()));
		gl_filePanel.setVerticalGroup(
				gl_filePanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_filePanel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_filePanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(selectFileButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(gl_filePanel.createSequentialGroup()
												.addGap(1)
												.addComponent(filePathTextField, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
								.addGap(7)));
		filePanel.setLayout(gl_filePanel);
		contentPanel.setLayout(gl_contentPanel);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		exportButton = new JButton(I18n.string("export.button.export"));
		exportButton.setEnabled(false);
		buttonPane.add(exportButton);
		getRootPane().setDefaultButton(exportButton);
		closeButton = new JButton(I18n.string("export.button.close"));
		buttonPane.add(closeButton);
		
		initializeButtons();
	}
	
	private void initializeButtons() {
		selectFileButton.addActionListener((event) -> openFileSelector());
		
		exportButton.addActionListener((event) -> startExport());
		closeButton.addActionListener((event) -> close());
	}
	
	private void openFileSelector() {
		FileDialog fileDialog = new FileDialog(BarCutOptimizerWindow.get().getWindow(), I18n.string("import.dialog.title"), FileDialog.SAVE);
		fileDialog.setLocationRelativeTo(null);
		fileDialog.setFile("*." + PDF_EXTENSION);
		fileDialog.setVisible(true);
		
		String directory = fileDialog.getDirectory();
		String filename = fileDialog.getFile();
		if (directory != null && filename != null) {
			if (!filename.endsWith("." + PDF_EXTENSION)) {
				filename += "." + PDF_EXTENSION;
			}
			
			targetFile = new File(directory, filename);
			filePathTextField.setText(targetFile.getAbsolutePath());
			exportButton.setEnabled(true);
		}
	}
	
	private void startExport() {
		lock();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new PdfDataExporter().attachCallback(new ExporterCallback() {
						@Override
						public void onInitialization(int etaCount) {
							etaProgressBar.setMaximum(etaCount);
							
							progressProgressBar.setMaximum(100);
							progressProgressBar.setStringPainted(true);
							
							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
						
						@Override
						public void onNextEta(String eta) {
							etaProgressBar.setValue(etaProgressBar.getValue() + 1);
							etaLabel.setText(I18n.string("export.pdf.eta." + eta));
						}
						
						@Override
						public void onProgressPublished(int current, int max) {
							// progressProgressBar.setMaximum(max);
							// progressProgressBar.setValue(current);
							
							progressProgressBar.setValue((int) ((current * 100.0f) / max));
							progressProgressBar.setString(current + " / " + max);
							
							// ThreadUtils.sleep(10);
						}
						
						@Override
						public void onFinished(File file) {
							JOptionPane.showMessageDialog(ExportDialog.this, I18n.string("export.eta.done", file.getAbsolutePath()), I18n.string("export.eta.done.dialog-title"), JOptionPane.INFORMATION_MESSAGE);
						}
					}).exportToFile(barReferences, targetFile);
				} catch (Exception exception) {
					exception.printStackTrace();
					JOptionPane.showMessageDialog(ExportDialog.this, I18n.string("export.error.generic"), I18n.string("dialog.error.title"), JOptionPane.INFORMATION_MESSAGE);
				}
				
				SwingUtilities.invokeLater(() -> close());
			}
		}).start();
	}
	
	private void lock() {
		for (JButton button : Arrays.asList(selectFileButton, exportButton, closeButton)) {
			button.setEnabled(false);
		}
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}
	
	private void close() {
		setVisible(false);
	}
	
	public static ExportDialog open(JFrame parent, List<BarReference> barReferences) {
		ExportDialog dialog = new ExportDialog(parent, barReferences);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
		return dialog;
	}
	
}