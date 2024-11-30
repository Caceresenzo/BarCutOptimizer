package dev.caceresenzo.barcutoptimizer.language;

import java.util.Locale;
import java.util.ResourceBundle;

import lombok.experimental.UtilityClass;

@UtilityClass
public class I18n {

	private static ResourceBundle resourceBundle;

	public static void load(Locale locale) {
		resourceBundle = ResourceBundle.getBundle("language", locale);
	}

	public static String string(String key) {
		return resourceBundle.getString(key);
	}

	public static String string(String formatKey, Object... arguments) {
		return String.format(resourceBundle.getString(formatKey), arguments);
	}

}