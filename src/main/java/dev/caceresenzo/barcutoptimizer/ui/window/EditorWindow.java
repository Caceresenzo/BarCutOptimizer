package dev.caceresenzo.barcutoptimizer.ui.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
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

import dev.caceresenzo.barcutoptimizer.constant.Assets;
import dev.caceresenzo.barcutoptimizer.constant.Defaults;
import dev.caceresenzo.barcutoptimizer.language.I18n;
import dev.caceresenzo.barcutoptimizer.model.BarReference;
import dev.caceresenzo.barcutoptimizer.model.Cut;
import dev.caceresenzo.barcutoptimizer.model.CutGroup;
import dev.caceresenzo.barcutoptimizer.ui.component.BarReferenceInfoPanel;
import dev.caceresenzo.barcutoptimizer.ui.component.CutGroupPanel;
import dev.caceresenzo.barcutoptimizer.ui.dialog.CutsEditionDialog;
import dev.caceresenzo.barcutoptimizer.ui.dialog.ExportDialog;
import dev.caceresenzo.barcutoptimizer.ui.other.ImportDialogs;
import dev.caceresenzo.barcutoptimizer.ui.other.NewBarReferenceDialogs;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class EditorWindow {

	/* Components */
	private @Getter JFrame frame;
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
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;

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
		frame.setTitle(I18n.string("application.title", Defaults.VERSION));

		JScrollPane treeScrollPanel = new JScrollPane();
		treeScrollPanel.setBorder(new TitledBorder(null, I18n.string("editor.panel.tree"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		treeScrollPanel.setViewportBorder(null);

		cutGroupListScrollPanel = new JScrollPane();
		cutGroupListScrollPanel.setBorder(new TitledBorder(null, I18n.string("editor.panel.cuts"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		addNewBarReferenceButton = new JButton(I18n.string("editor.button.add-new-bar-reference"));

		exportButton = new JButton(I18n.string("editor.button.export"));

		editCutsButton = new JButton(I18n.string("editor.button.edit-cuts"));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(addNewBarReferenceButton, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
						.addComponent(treeScrollPanel, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(editCutsButton, GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(exportButton, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
						.addComponent(cutGroupListScrollPanel, GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(cutGroupListScrollPanel, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addComponent(treeScrollPanel, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(editCutsButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
						.addComponent(exportButton, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
						.addComponent(addNewBarReferenceButton, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);

		cutGroupListContainerPanel = new JPanel();
		cutGroupListScrollPanel.setViewportView(cutGroupListContainerPanel);
		cutGroupListContainerPanel.setLayout(new MigLayout("flowy", "[grow,fill]", "[fill]"));

		cutGroupListScrollPanel.getVerticalScrollBar().setUnitIncrement(20);

		rootNode = new DefaultMutableTreeNode(I18n.string("editor.tree.root"));
		tree = new JTree(rootNode);
		tree.setBackground(UIManager.getColor("Button.background"));
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

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		menu = new JMenu(I18n.string("editor.menu.import"));
		menuBar.add(menu);

		menuItem = new JMenuItem(I18n.string("editor.menu.import.easywin"), new ImageIcon(EditorWindow.class.getResource(Assets.ICON_PDF_SMALL)));
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ImportDialogs.get().startImportationProcess();
			}

		});
		menu.add(menuItem);
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

						label.setText(I18n.string("editor.tree.item.bar-reference.format", barReference.getName()));
					}

					if (object instanceof CutGroup) {
						CutGroup cutGroup = (CutGroup) object;

						label.setText(I18n.string("editor.tree.item.cut-group.format", cutGroup.getBarLength(), cutGroup.getCutCount()));
					}

					if (object instanceof Cut) {
						Cut cut = (Cut) object;

						label.setText(I18n.string("editor.tree.item.cut.format", cut.getLength(), cut.getLeftAngle(), cut.getRightAngle()));
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

				if (userObject instanceof BarReference) {
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

				displayBarReference(barReference);
			}

		});

		exportButton.addActionListener((event) -> ExportDialog.open(getFrame(), new ArrayList<>(barReferences)));

		editCutsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				CutsEditionDialog.open(getFrame(), currentBarReference, new CutsEditionDialog.Callback() {

					@Override
					public void onFinish(BarReference barReference) {
						displayBarReference(barReference);
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
			rootNode.add(new DefaultMutableTreeNode(barReference));
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
		if (userObject instanceof BarReference) {
			displayBarReference((BarReference) userObject);
		}
	}

	private void displayBarReference(BarReference barReference) {
		clearDisplaySection();

		currentBarReference = barReference;

		List<CutGroup> cutGroups = barReference.getCutGroups();

		if (!cutGroups.isEmpty()) {
			cutGroupListContainerPanel.add(new BarReferenceInfoPanel(barReference));

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
			JMenuItem emptyMenuItem = new JMenuItem(I18n.string("editor.tree.popup-menu.item.empty"), new ImageIcon(EditorWindow.class.getResource(Assets.ICON_SHREDDER_SMALL)));
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

			JMenuItem deleteMenuItem = new JMenuItem(I18n.string("editor.tree.popup-menu.item.remove"), new ImageIcon(EditorWindow.class.getResource(Assets.ICON_DELETE_SMALL)));
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

	public static final EditorWindow open(List<BarReference> barReferences) {
		EditorWindow window = new EditorWindow(barReferences);
		window.frame.setVisible(true);

		return window;
	}

}