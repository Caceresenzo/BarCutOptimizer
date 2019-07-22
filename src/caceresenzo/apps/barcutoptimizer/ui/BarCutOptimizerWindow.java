package caceresenzo.apps.barcutoptimizer.ui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.ui.windows.EditorWindow;
import caceresenzo.apps.barcutoptimizer.ui.windows.StartWindow;

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