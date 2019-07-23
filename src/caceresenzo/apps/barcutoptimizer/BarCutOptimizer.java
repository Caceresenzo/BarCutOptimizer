package caceresenzo.apps.barcutoptimizer;

import caceresenzo.apps.barcutoptimizer.config.Language;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager;
import caceresenzo.apps.barcutoptimizer.ui.BarCutOptimizerWindow;
import caceresenzo.libs.logger.Logger;

public class BarCutOptimizer {
	
	public static void main(String[] args) {
		Logger.setStaticLength(20);
		
		Language.get().initialize();
		AlgorithmManager.get().initialize();
		BarCutOptimizerWindow.get().initialize();
		
		BarCutOptimizerWindow.get().openStart();
	}
	
}
