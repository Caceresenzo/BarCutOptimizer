package caceresenzo.apps.barcutoptimizer.ui.others;

import java.awt.FileDialog;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import caceresenzo.apps.barcutoptimizer.assets.Assets;
import caceresenzo.apps.barcutoptimizer.config.Constants;
import caceresenzo.apps.barcutoptimizer.logic.importer.DataImporter;
import caceresenzo.apps.barcutoptimizer.logic.importer.implementations.EasyWinFormatDataImporter;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.ui.BarCutOptimizerWindow;
import caceresenzo.apps.barcutoptimizer.ui.dialogs.ProgressDialog;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.string.StringUtils;

public class ImportDialogs implements Constants {
	
	/* Singleton */
	private static ImportDialogs INSTANCE;
	
	/* Dialog */
	private final ProgressDialog progressDialog;
	
	/* Private Constructor */
	private ImportDialogs() {
		this.progressDialog = new ProgressDialog(null, true, i18n.string("dialog.loading-import.title"), i18n.string("dialog.loading-import.message"));
		this.progressDialog.getProgressBar().setIndeterminate(true);
	}
	
	public void startImportationProcess() {
		FileDialog fileDialog = new FileDialog(BarCutOptimizerWindow.get().getWindow(), i18n.string("import.dialog.title"), FileDialog.LOAD);
		fileDialog.setLocationRelativeTo(null);
		fileDialog.setFile("*." + PDF_EXTENSION);
		fileDialog.setFilenameFilter((dir, name) -> name.endsWith("." + PDF_EXTENSION));
		fileDialog.setVisible(true);
		
		String directory = fileDialog.getDirectory();
		String filename = fileDialog.getFile();
		if (directory != null && filename != null) {
			File file = new File(directory, filename);
			
			load(file);
		}
	}
	
	public void forceImportProcess(File file) {
		load(file);
	}
	
	private void load(File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				progressDialog.open();
				
				if (!file.exists() || !file.canRead()) {
					showError(i18n.string("import.error.file-not-accessible"));
					return;
				}
				
				DataImporter importer = new EasyWinFormatDataImporter();
				try {
					List<BarReference> barReferences = importer.loadFromFile(file);
					
					if (barReferences == null || barReferences.isEmpty()) {
						showError(i18n.string("import.error.no-bar-found"));
						return;
					}

					progressDialog.close();
					
					BarCutOptimizerWindow.get().closeCurrent();
					BarCutOptimizerWindow.get().openEditor(barReferences);			
				} catch (Exception exception) {
					exception.printStackTrace();
					showError(i18n.string("import.error.failed-to-import", StringUtils.fromException(exception)));
				}
			}
		}).start();
	}
	
	private void showError(String description) {
		progressDialog.close();
		
		JOptionPane.showMessageDialog(BarCutOptimizerWindow.get().getWindow(), description, i18n.string("dialog.error.title"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ImportDialogs.class.getResource(Assets.ICON_ERROR)));
	}
	
	/** @return ImportDialogs's singleton instance. */
	public static final ImportDialogs get() {
		if (INSTANCE == null) {
			INSTANCE = new ImportDialogs();
		}
		
		return INSTANCE;
	}
	
}