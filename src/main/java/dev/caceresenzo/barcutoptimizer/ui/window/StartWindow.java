package dev.caceresenzo.barcutoptimizer.ui.window;

import java.io.File;

import dev.caceresenzo.barcutoptimizer.BarCutOptimizer;
import dev.caceresenzo.barcutoptimizer.ui.other.ImportDialogs;

@Deprecated
public class StartWindow {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		BarCutOptimizer.main(args);
		ImportDialogs.get().forceImportProcess(new File("BON DE FAB (5).pdf"));
	}

}