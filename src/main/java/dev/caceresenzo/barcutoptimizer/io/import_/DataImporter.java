package dev.caceresenzo.barcutoptimizer.io.import_;

import java.io.File;
import java.util.List;

import dev.caceresenzo.barcutoptimizer.model.BarReference;

public interface DataImporter {
	
	public List<BarReference> loadFromFile(File file) throws Exception;
	
}