package caceresenzo.apps.barcutoptimizer.ui.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import caceresenzo.apps.barcutoptimizer.assets.Assets;
import caceresenzo.apps.barcutoptimizer.config.Constants;
import caceresenzo.apps.barcutoptimizer.logic.exporter.DataExporter;
import caceresenzo.apps.barcutoptimizer.logic.exporter.ExporterCallback;
import caceresenzo.apps.barcutoptimizer.logic.exporter.implementations.PdfDataExporter;
import caceresenzo.apps.barcutoptimizer.models.BarReference;
import caceresenzo.apps.barcutoptimizer.models.Cut;
import caceresenzo.apps.barcutoptimizer.models.CutGroup;
import caceresenzo.apps.barcutoptimizer.ui.components.CutGroupPanel;
import caceresenzo.apps.barcutoptimizer.ui.dialogs.CutsEditionDialog;
import caceresenzo.apps.barcutoptimizer.ui.dialogs.ExportDialog;
import caceresenzo.apps.barcutoptimizer.ui.others.NewBarReferenceDialogs;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;

public class EditorWindow implements Constants {
	
	/* Components */
	private JFrame frame;
	private BarReference currentBarReference;
	private JPanel cutGroupListContainerPanel;
	private JScrollPane cutGroupListScrollPanel;
	private JButton addNewBarReferenceButton;
	private JButton editCutsButton;
	private JTree tree;
	private JButton exportButton;
	
	/* Variables */
	private List<BarReference> barReferences;
	private DefaultMutableTreeNode rootNode;
	
	/**
	 * Create the application.
	 */
	public EditorWindow(List<BarReference> barReferences) {
		this.barReferences = barReferences;
		
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setSize(689, 440);
		frame.setMinimumSize(new Dimension(800, 500));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(i18n.string("application.title"));
		
		JScrollPane treeScrollPanel = new JScrollPane();
		treeScrollPanel.setViewportBorder(null);
		
		cutGroupListScrollPanel = new JScrollPane();
		
		addNewBarReferenceButton = new JButton(i18n.string("editor.button.add-new-bar-reference"));
		
		exportButton = new JButton(i18n.string("editor.button.export"));
		
		editCutsButton = new JButton(i18n.string("editor.button.edit-cuts"));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(addNewBarReferenceButton, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(exportButton, GroupLayout.PREFERRED_SIZE, 252, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(editCutsButton, GroupLayout.PREFERRED_SIZE, 249, Short.MAX_VALUE))
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(treeScrollPanel, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(cutGroupListScrollPanel, GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)))
								.addContainerGap()));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(cutGroupListScrollPanel, GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
										.addComponent(treeScrollPanel, GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE, false)
												.addComponent(exportButton)
												.addComponent(editCutsButton))
										.addComponent(addNewBarReferenceButton))
								.addContainerGap()));
		
		cutGroupListContainerPanel = new JPanel();
		cutGroupListScrollPanel.setViewportView(cutGroupListContainerPanel);
		cutGroupListContainerPanel.setLayout(new BoxLayout(cutGroupListContainerPanel, BoxLayout.Y_AXIS));
		
		cutGroupListScrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		
		rootNode = new DefaultMutableTreeNode(i18n.string("editor.tree.root"));
		tree = new JTree(rootNode);
		tree.setBorder(null);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeScrollPanel.setViewportView(tree);
		
		frame.getContentPane().setLayout(groupLayout);
		
		initializeTree();
		initializeButtons();
		
		if (!barReferences.isEmpty()) {
			displayBarReference(barReferences.get(0));
		} else {
			editCutsButton.setEnabled(false);
		}
		
		frame.pack();
	}
	
	private void initializeTree() {
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				
				if (value instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) value;
					Object object = defaultMutableTreeNode.getUserObject();
					
					if (object instanceof BarReference) {
						BarReference barReference = (BarReference) object;
						
						label.setText(i18n.string("editor.tree.item.bar-reference.format", barReference.getName()));
					}
					
					if (object instanceof CutGroup) {
						CutGroup cutGroup = (CutGroup) object;
						
						label.setText(i18n.string("editor.tree.item.cut-group.format", cutGroup.getBarLength(), cutGroup.getCutCount()));
					}
					
					if (object instanceof Cut) {
						Cut cut = (Cut) object;
						
						label.setText(i18n.string("editor.tree.item.cut.format", cut.getLength(), cut.getCutAngleA(), cut.getCutAngleB()));
					}
				}
				
				return label;
			};
		});
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				TreePath treePath = tree.getPathForLocation(event.getX(), event.getY());
				
				if (treePath != null) {
					DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
					
					displayNodeContent(defaultMutableTreeNode);
					
					if (event.getButton() == MouseEvent.BUTTON3) {
						createTreePopupMenu(event);
					}
				} else {
					clearDisplaySection();
				}
			}
		});
		
		createTreeNodes();
		expandAllNodes(tree, 0, tree.getRowCount(), false);
		
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				;
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				TreePath path = event.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object userObject = node.getUserObject();
				
				if (userObject instanceof CutGroup || userObject instanceof BarReference) {
					return;
				}
				
				throw new ExpandVetoException(event, "Collapsing tree not allowed");
			}
		});
	}
	
	private void initializeButtons() {
		addNewBarReferenceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				BarReference barReference = NewBarReferenceDialogs.get().openBarReferenceCreationDialog();
				
				if (barReference == null) {
					return;
				}
				
				barReferences.add(barReference);
				createTreeNodes();
				reloadTreeNode(null);
			}
		});
		
		exportButton.addActionListener((event) -> ExportDialog.open(getFrame(), new ArrayList<>(barReferences)));
		
		editCutsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				CutsEditionDialog.open(getFrame(), currentBarReference, new CutsEditionDialog.Callback() {
					@Override
					public void onFinish(BarReference barReference, boolean hasDoneOptimization) {
						if (hasDoneOptimization) {
							displayBarReference(barReference);
						}
						
						Logger.info("Received callback from the cut editor dialog. (hashasDoneOptimization? %s)", hasDoneOptimization);
					}
					
					@Override
					public void onCancel(BarReference barReference) {
						;
					}
					
					@Override
					public void onException(BarReference barReference, Exception exception) {
						;
					}
				});
				
			}
		});
	}
	
	private void createTreeNodes() {
		rootNode.removeAllChildren();
		
		for (BarReference barReference : barReferences) {
			DefaultMutableTreeNode barReferenceNode = new DefaultMutableTreeNode(barReference);
			
			if (SHOW_CUT_GROUPS_IN_EDITOR_TREE) {
				for (CutGroup cutGroup : barReference.getCutGroups()) {
					DefaultMutableTreeNode cutGroupNode = new DefaultMutableTreeNode(cutGroup);
					
					if (SHOW_CUTS_IN_EDITOR_TREE) {
						for (Cut cut : cutGroup.getCuts()) {
							DefaultMutableTreeNode cutNode = new DefaultMutableTreeNode(cut);
							
							cutGroupNode.add(cutNode);
						}
					}
					
					barReferenceNode.add(cutGroupNode);
				}
			}
			
			rootNode.add(barReferenceNode);
		}
	}
	
	private void reloadTreeNode(TreeNode node) {
		((DefaultTreeModel) tree.getModel()).reload(node == null ? rootNode : node);
	}
	
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount, boolean recursive) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}
		
		if (recursive) {
			if (tree.getRowCount() != rowCount) {
				expandAllNodes(tree, rowCount, tree.getRowCount(), recursive);
			}
		}
	}
	
	private void displayNodeContent(DefaultMutableTreeNode defaultMutableTreeNode) {
		clearDisplaySection();
		
		Object userObject = defaultMutableTreeNode.getUserObject();
		
		if (userObject instanceof CutGroup) {
			userObject = ((DefaultMutableTreeNode) defaultMutableTreeNode.getParent()).getUserObject();
		}
		
		if (userObject instanceof BarReference) {
			displayBarReference((BarReference) userObject);
		}
	}
	
	private void displayBarReference(BarReference barReference) {
		clearDisplaySection();
		
		currentBarReference = barReference;
		
		List<CutGroup> cutGroups = barReference.getCutGroups();
		
		if (!cutGroups.isEmpty()) {
			cutGroups.forEach((cutGroup) -> {
				cutGroupListContainerPanel.add(new CutGroupPanel(cutGroup));
			});
		} else {
			cutGroupListContainerPanel.add(new JLabel("Aucune coupe"));
		}
		
		editCutsButton.setEnabled(true);
		cutGroupListScrollPanel.getVerticalScrollBar().setValue(0);
	}
	
	private void clearDisplaySection() {
		currentBarReference = null;
		
		cutGroupListContainerPanel.removeAll();
		cutGroupListContainerPanel.revalidate();
		cutGroupListContainerPanel.repaint();
		
		editCutsButton.setEnabled(false);
	}
	
	private void createTreePopupMenu(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		
		TreePath path = tree.getPathForLocation(x, y);
		
		if (path == null) {
			return;
		}
		
		tree.setSelectionPath(path);
		
		JPopupMenu popup = new JPopupMenu();
		
		DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		Object userObject = defaultMutableTreeNode.getUserObject();
		
		if (rootNode.equals(defaultMutableTreeNode)) {
			JMenuItem emptyMenuItem = new JMenuItem(i18n.string("editor.tree.popup-menu.item.empty"), new ImageIcon(EditorWindow.class.getResource(Assets.ICON_SHREDDER_SMALL)));
			emptyMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					barReferences.clear();
					
					rootNode.removeAllChildren();
					reloadTreeNode(rootNode);
				}
			});
			
			popup.add(emptyMenuItem);
		} else {
			CutGroup cutGroup = null;
			BarReference barReference = null;
			
			if (userObject instanceof CutGroup) {
				cutGroup = (CutGroup) userObject;
				
				userObject = ((DefaultMutableTreeNode) defaultMutableTreeNode.getParent()).getUserObject();
			}
			
			if (userObject instanceof BarReference) {
				barReference = (BarReference) userObject;
			}
			
			if (barReference == null) {
				return;
			}
			
			final CutGroup finalCutGroup = cutGroup;
			final BarReference finalBarReference = barReference;
			
			JMenuItem deleteMenuItem = new JMenuItem(i18n.string("editor.tree.popup-menu.item.remove"), new ImageIcon(EditorWindow.class.getResource(Assets.ICON_DELETE_SMALL)));
			deleteMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					TreeNode nodeToReload;
					
					if (finalCutGroup != null) {
						finalBarReference.getCutGroups().remove(finalCutGroup);
						
						nodeToReload = defaultMutableTreeNode.getParent();
					} else {
						barReferences.remove(finalBarReference);
						
						nodeToReload = rootNode;
					}
					
					((MutableTreeNode) nodeToReload).remove(defaultMutableTreeNode);
					reloadTreeNode(nodeToReload);
				}
			});
			
			popup.add(deleteMenuItem);
		}
		
		popup.show(tree, x, y);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public static final EditorWindow open(List<BarReference> barReferences) {
		EditorWindow window = new EditorWindow(barReferences);
		window.frame.setVisible(true);
		
		return window;
	}
	
	public JButton getButton() {
		return exportButton;
	}
}