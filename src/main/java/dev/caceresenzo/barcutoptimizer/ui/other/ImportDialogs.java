package dev.caceresenzo.barcutoptimizer.ui.other;

import java.awt.FileDialog;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import dev.caceresenzo.barcutoptimizer.constant.Assets;
import dev.caceresenzo.barcutoptimizer.constant.Defaults;
import dev.caceresenzo.barcutoptimizer.io.import_.DataImporter;
import dev.caceresenzo.barcutoptimizer.io.import_.EasyWinFormatDataImporter;
import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.model.BarReference;
import dev.caceresenzo.barcutoptimizer.ui.BarCutOptimizerWindow;
import dev.caceresenzo.barcutoptimizer.ui.dialog.ProgressDialog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportDialogs {

	/* Singleton */
	private static ImportDialogs INSTANCE;

	/* Dialog */
	private final ProgressDialog progressDialog;

	/* Private Constructor */
	/**
	 * @wbp.parser.entryPoint
	 */
	private ImportDialogs() {
		this.progressDialog = new ProgressDialog(null, true, I18n.string("dialog.loading-import.title"), I18n.string("dialog.loading-import.message"));
		this.progressDialog.getProgressBar().setIndeterminate(true);
	}

	public void startImportationProcess() {
		FileDialog fileDialog = new FileDialog(BarCutOptimizerWindow.get().getWindow(), I18n.string("import.dialog.title"), FileDialog.LOAD);
		fileDialog.setLocationRelativeTo(null);
		fileDialog.setFile("*." + Defaults.PDF_EXTENSION);
		fileDialog.setFilenameFilter((dir, name) -> name.endsWith("." + Defaults.PDF_EXTENSION));
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
					showError(I18n.string("import.error.file-not-accessible"));
					return;
				}

				DataImporter importer = new EasyWinFormatDataImporter();
				try {
					List<BarReference> barReferences = importer.loadFromFile(file);

					if (barReferences == null || barReferences.isEmpty()) {
						showError(I18n.string("import.error.no-bar-found"));
						return;
					}

					progressDialog.close();

					BarCutOptimizerWindow.get().closeCurrent();
					BarCutOptimizerWindow.get().openEditor(barReferences);
				} catch (Exception exception) {
					log.error("Could not load file", exception);

					showError(I18n.string("import.error.failed-to-import", exception.getCause()));
				}
			}

		}).start();
	}

	private void showError(String description) {
		progressDialog.close();

		JOptionPane.showMessageDialog(BarCutOptimizerWindow.get().getWindow(), description, I18n.string("dialog.error.title"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(ImportDialogs.class.getResource(Assets.ICON_ERROR)));
	}

	/** @return ImportDialogs's singleton instance. */
	public static final ImportDialogs get() {
		if (INSTANCE == null) {
			INSTANCE = new ImportDialogs();
		}

		return INSTANCE;
	}

}