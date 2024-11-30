package dev.caceresenzo.barcutoptimizer.ui.windows;

import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dev.caceresenzo.barcutoptimizer.BarCutOptimizer;
import dev.caceresenzo.barcutoptimizer.assets.Assets;
import dev.caceresenzo.barcutoptimizer.config.Constants;
import dev.caceresenzo.barcutoptimizer.config.I18n;
import dev.caceresenzo.barcutoptimizer.ui.components.BigButton;
import dev.caceresenzo.barcutoptimizer.ui.others.CreateNewDialogs;
import dev.caceresenzo.barcutoptimizer.ui.others.ImportDialogs;

public class StartWindow {
	
	private JFrame frame;
	private BigButton newBigButton;
	private BigButton importBigButton;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		BarCutOptimizer.main(args);
		ImportDialogs.get().forceImportProcess(new File("C:\\Users\\cacer\\Downloads\\BON DE FAB (5).pdf"));
		// BarCutOptimizerWindow.get().openStart();
	}
	
	/**
	 * Create the application.
	 */
	private StartWindow() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setSize(480, 300);
		frame.setTitle(I18n.string("application.title", Constants.VERSION));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		newBigButton = new BigButton(I18n.string("start-window.button.new.html"), Assets.ICON_CREATE, (bigButton) -> CreateNewDialogs.get().openEditor());
		panel.add(newBigButton);
		
		importBigButton = new BigButton(I18n.string("start-window.button.import.html"), Assets.ICON_PDF, (bigButton) -> ImportDialogs.get().startImportationProcess());
		panel.add(importBigButton);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public static final StartWindow open() {
		StartWindow window = new StartWindow();
		window.frame.setVisible(true);
		
		return window;
	}
	
}
