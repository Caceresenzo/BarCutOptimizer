package dev.caceresenzo.barcutoptimizer.io.export;

import java.io.File;
import java.util.List;

import dev.caceresenzo.barcutoptimizer.model.BarReference;

public interface DataExporter {
	
	public void exportToFile(List<BarReference> barReferences, File file) throws Exception;
	
	public String formatFilename(File originalFile);
	
	public DataExporter attachCallback(ExporterCallback exporterCallback);
	
}