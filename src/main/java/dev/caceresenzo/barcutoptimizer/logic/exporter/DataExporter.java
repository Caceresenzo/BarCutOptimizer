package dev.caceresenzo.barcutoptimizer.logic.exporter;

import java.io.File;
import java.util.List;

import dev.caceresenzo.barcutoptimizer.models.BarReference;

public interface DataExporter {
	
	public void exportToFile(List<BarReference> barReferences, File file) throws Exception;
	
	public String formatFilename(File originalFile);
	
	public DataExporter attachCallback(ExporterCallback exporterCallback);
	
}