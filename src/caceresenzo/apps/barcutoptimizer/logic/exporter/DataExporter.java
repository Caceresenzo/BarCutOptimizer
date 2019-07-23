package caceresenzo.apps.barcutoptimizer.logic.exporter;

import java.io.File;
import java.util.List;

import caceresenzo.apps.barcutoptimizer.models.BarReference;

public interface DataExporter {
	
	public void exportToFile(List<BarReference> barReferences, File file) throws Exception;
	
	public String formatFilename(File originalFile);
	
}