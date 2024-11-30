package dev.caceresenzo.barcutoptimizer.ui.others;

import java.util.ArrayList;

import dev.caceresenzo.barcutoptimizer.ui.BarCutOptimizerWindow;

public class CreateNewDialogs {
	
	/* Singleton */
	private static CreateNewDialogs INSTANCE;
	
	/* Private Constructor */
	private CreateNewDialogs() {
		;
	}
	
	public void openEditor() {
		BarCutOptimizerWindow.get().closeCurrent();
		BarCutOptimizerWindow.get().openEditor(new ArrayList<>());
	}
	
	/** @return CreateNewDialogs's singleton instance. */
	public static final CreateNewDialogs get() {
		if (INSTANCE == null) {
			INSTANCE = new CreateNewDialogs();
		}
		
		return INSTANCE;
	}
	
}