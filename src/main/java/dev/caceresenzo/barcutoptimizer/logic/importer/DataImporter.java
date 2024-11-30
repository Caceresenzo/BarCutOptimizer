package dev.caceresenzo.barcutoptimizer.logic.importer;

import java.io.File;
import java.util.List;

import dev.caceresenzo.barcutoptimizer.models.BarReference;

public interface DataImporter {
	
	public List<BarReference> loadFromFile(File file) throws Exception;
	
}