package caceresenzo.apps.barcutoptimizer.ui.dialogs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog {
	
	/* Components */
	private JProgressBar progressBar;
	private JLabel label;
	
	/* Private variable */
	private final boolean closingStopApplication;
	
	/**
	 * Create the dialog.
	 */
	public ProgressDialog(JFrame parent, boolean closingStopApplication, String title, String message) {
		super(parent);
		
		this.closingStopApplication = closingStopApplication;
		
		setSize(450, 160);
		setLocationRelativeTo(null);
		setModal(true);
		setTitle(title);
		setResizable(false);
		
		label = new JLabel(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 10f));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		progressBar = new JProgressBar();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
										.addComponent(label, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(label, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
								.addContainerGap()));
		getContentPane().setLayout(groupLayout);
		
		if (closingStopApplication) {
			addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent event) {
					exitApplication();
				}
				
				public void windowClosing(WindowEvent event) {
					exitApplication();
				}
				
				private void exitApplication() {
					System.exit(0);
				}
			});
		}
		
	}
	
	public void open() {
		SwingUtilities.invokeLater(() -> setVisible(true));
	}
	
	public void close() {
		SwingUtilities.invokeLater(() -> setVisible(false));
	}
	
	public boolean isClosingStopApplication() {
		return closingStopApplication;
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public JLabel getLabel() {
		return label;
	}
	
}