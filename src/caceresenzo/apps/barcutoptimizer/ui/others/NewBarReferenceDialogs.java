package caceresenzo.apps.barcutoptimizer.ui.others;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.ui.BarCutOptimizerWindow;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.string.StringUtils;

public class NewBarReferenceDialogs {
	
	/* Singleton */
	private static NewBarReferenceDialogs INSTANCE;
	
	/* Private Constructor */
	private NewBarReferenceDialogs() {
		;
	}
	
	public BarReference openBarReferenceCreationDialog() {
		String name = JOptionPane.showInputDialog(BarCutOptimizerWindow.get().getWindow(), i18n.string("dialog.new-bar-reference.message"));
		
		if (!StringUtils.validate(name)) {
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