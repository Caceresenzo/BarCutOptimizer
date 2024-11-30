package dev.caceresenzo.barcutoptimizer.ui.other;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.model.BarReference;
import dev.caceresenzo.barcutoptimizer.ui.BarCutOptimizerWindow;

public class NewBarReferenceDialogs {
	
	/* Singleton */
	private static NewBarReferenceDialogs INSTANCE;
	
	/* Private Constructor */
	private NewBarReferenceDialogs() {
		;
	}
	
	public BarReference openBarReferenceCreationDialog() {
		String name = JOptionPane.showInputDialog(BarCutOptimizerWindow.get().getWindow(), I18n.string("dialog.new-bar-reference.message"));
		
		if (StringUtils.isBlank(name)) {
			return null;
		}
		
		return new BarReference(name, new ArrayList<>());
	}
	
	/** @return NewBarReferenceDialogs's singleton instance. */
	public static final NewBarReferenceDialogs get() {
		if (INSTANCE == null) {
			INSTANCE = new NewBarReferenceDialogs();
		}
		
		return INSTANCE;
	}
	
}