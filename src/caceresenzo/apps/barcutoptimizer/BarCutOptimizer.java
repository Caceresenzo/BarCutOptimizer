package caceresenzo.apps.barcutoptimizer;

import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import caceresenzo.apps.barcutoptimizer.config.Language;
import caceresenzo.apps.barcutoptimizer.logic.algorithms.AlgorithmManager;
import caceresenzo.apps.barcutoptimizer.ui.BarCutOptimizerWindow;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

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
		
		File fileToOpen = parseCommandLineInterface(args);
		
		Language.get().initialize();
		AlgorithmManager.get().initialize();
		BarCutOptimizerWindow.get().initialize();
		
		if (fileToOpen == null) {
			BarCutOptimizerWindow.get().openStart();
		} else {
			BarCutOptimizerWindow.get().openFile(fileToOpen);
		}
	}
	
	private static File parseCommandLineInterface(String[] args) {
		Options options = new Options();
		
		Option inputOption = new Option("i", "input", true, "input file");
		inputOption.setRequired(false);
		options.addOption(inputOption);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException exception) {
			Logger.exception(exception, "Failed to parse command line.");
			
			formatter.printHelp(i18n.string("application.title"), options);
			
			JOptionPane.showMessageDialog(null, i18n.getString("error.parse-cli", exception.getLocalizedMessage()), i18n.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
			
			System.exit(1);
		}
		
		String filePath = commandLine.getOptionValue(inputOption.getLongOpt());
		
		if (StringUtils.validate(filePath)) {
			return new File(filePath);
		}
		
		return null;
	}
	
}
