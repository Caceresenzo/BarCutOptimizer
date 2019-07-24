package caceresenzo.apps.barcutoptimizer;

import java.io.File;
import java.net.URISyntaxException;

import caceresenzo.apps.barcutoptimizer.config.Language;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager;
import caceresenzo.apps.barcutoptimizer.ui.BarCutOptimizerWindow;
import caceresenzo.libs.logger.Logger;

public class BarCutOptimizer {
	
	public static final File JAR_FILE;
	public static final File APPLICATION_FOLDER;
	public static final File CACHE_FOLDER;
	
	static {
		File jarFile = null;
		try {
			jarFile = new File(BarCutOptimizer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException exception) {
			System.err.println("Failed to locate the jar archive file.");
			System.exit(1);
		}
		
		JAR_FILE = jarFile;
		APPLICATION_FOLDER = JAR_FILE.getParentFile();
		CACHE_FOLDER = new File(APPLICATION_FOLDER, "cache");
		
		CACHE_FOLDER.deleteOnExit();
	}
	
	public static void main(String[] args) {
		Logger.setStaticLength(20);
		
		Language.get().initialize();
		AlgorithmManager.get().initialize();
		BarCutOptimizerWindow.get().initialize();
		
		BarCutOptimizerWindow.get().openStart();
	}
	
}
