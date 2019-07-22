package caceresenzo.apps.barcutoptimizer.logic.importer;

import java.io.File;
import java.util.List;

import caceresenzo.apps.barcutoptimizer.models.BarReference;

public interface DataImporter {
	
	public List<BarReference> loadFromFile(File file) throws Exception;
	
}