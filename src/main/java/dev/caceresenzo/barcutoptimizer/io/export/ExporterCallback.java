package dev.caceresenzo.barcutoptimizer.io.export;

import java.io.File;

public interface ExporterCallback {
	
	public void onInitialization(int etaCount);
	
	public void onNextEta(String eta);

	public void onProgressPublished(int current, int max);
	
	public void onFinished(File file);
	
}