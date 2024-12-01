package dev.caceresenzo.barcutoptimizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.optimize.AlgorithmManager;
import dev.caceresenzo.barcutoptimizer.ui.BarCutOptimizerWindow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BarCutOptimizer {

	public static void main(String[] args) {
		I18n.load(Locale.FRENCH);

		File fileToOpen = parseCommandLineInterface(args);

		AlgorithmManager.get().initialize();
		BarCutOptimizerWindow.get().initialize();

		if (fileToOpen == null) {
			BarCutOptimizerWindow.get().openEditor(new ArrayList<>());
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
			log.error("Failed to parse command line", exception);

			formatter.printHelp(I18n.string("application.title"), options);

			JOptionPane.showMessageDialog(null, I18n.string("error.parse-cli", exception.getLocalizedMessage()), I18n.string("dialog.error.title"), JOptionPane.ERROR_MESSAGE);

			System.exit(1);
		}

		String filePath = commandLine.getOptionValue(inputOption.getLongOpt());

		if (StringUtils.isNotBlank(filePath)) {
			return new File(filePath);
		}

		return null;
	}

}
