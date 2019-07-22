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
import caceresenzo.libs.string.StringUtils;

public class ImportDialogs implements Constants {
	
	/* Singleton */
	private static ImportDialogs INSTANCE;
	
	/* Private Constructor */
	private ImportDialogs() {
		;
	}
	
	public void startImportationProcess() {
		FileDialog fileDialog = new FileDialog(BarCutOptimizerWindow.get().getWindow(), "Choose a file", FileDialog.LOAD);
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
		if (!file.exists() || !file.canRead()) {
			showError("Le fichier n'est pas lisible ou accessible.");
			return;
		}
		
		DataImporter importer = new EasyWinFormatDataImporter();
		try {
			List<BarReference> barReferences = importer.loadFromFile(file);
			
			if (barReferences == null || barReferences.isEmpty()) {
				showError("Aucune barre n'a été trouvé.");
				return;
			}
			
			BarCutOptimizerWindow.get().closeCurrent();
			BarCutOptimizerWindow.get().openEditor(barReferences);
		} catch (Exception exception) {
			exception.printStackTrace();
			showError("Erreur lors de l'importation: " + StringUtils.fromException(exception));
		}
	}
	
	private void showError(String description) {
		JOptionPane.showMessageDialog(BarCutOptimizerWindow.get().getWindow(), description, "Erreur", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ImportDialogs.class.getResource(Assets.ICON_ERROR)));
	}
	
	/** @return ImportDialogs's singleton instance. */
	public static final ImportDialogs get() {
		if (INSTANCE == null) {
			INSTANCE = new ImportDialogs();
		}
		
		return INSTANCE;
	}
	
}