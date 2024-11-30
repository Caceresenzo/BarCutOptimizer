package dev.caceresenzo.barcutoptimizer.ui;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import dev.caceresenzo.barcutoptimizer.models.BarReference;
import dev.caceresenzo.barcutoptimizer.ui.others.ImportDialogs;
import dev.caceresenzo.barcutoptimizer.ui.windows.EditorWindow;
import dev.caceresenzo.barcutoptimizer.ui.windows.StartWindow;

public class BarCutOptimizerWindow {
	
	/* Singleton */
	private static BarCutOptimizerWindow INSTANCE;
	
	/* Window */
	private JFrame window;
	
	/* Private Constructor */
	private BarCutOptimizerWindow() {
		;
	}
	
	public void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void openStart() {
		window = StartWindow.open().getFrame();
	}
	
	public void openFile(File fileToOpen) {
		ImportDialogs.get().forceImportProcess(fileToOpen);
	}
	
	public void openEditor(List<BarReference> barReferences) {
		window = EditorWindow.open(barReferences).getFrame();
	}
	
	public JFrame getWindow() {
		return window;
	}
	
	public boolean closeCurrent() {
		if (window == null) {
			return false;
		}
		
		window.setVisible(false);
		
		return true;
	}
	
	/** @return BarCutOptimizerWindow's singleton instance. */
	public static final BarCutOptimizerWindow get() {
		if (INSTANCE == null) {
			INSTANCE = new BarCutOptimizerWindow();
		}
		
		return INSTANCE;
	}
	
}