//This file is part of BuildGDX.
//Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Launcher.desktop.Components;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

public class DirectoryBrowser extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTree tree;
	private File selected;
	private JScrollPane scroll;

	public void pathList(String path, List<String> list) {
		int pos = 0;
		while (pos < path.length() && (path.charAt(pos) != '/' && path.charAt(pos) != File.separatorChar))
			pos++;
		String out = path.substring(0, pos);
		if (!out.isEmpty())
			list.add(out);
		if (pos < path.length())
			pathList(path.substring(pos + 1), list);
	}

	public int expandTree(JTree tree, DefaultTreeModel model, Object parent, List<String> pathtree, int pos) {
		if (parent instanceof FileTreeNode)
			scanChildren(model, (FileTreeNode) parent);

		for (int i = 0; i < model.getChildCount(parent); i++) {
			Object node = model.getChild(parent, i);
			if (node.toString().equals(File.separator)) { // Linux FileSystem
				pos = pos + 1;
				tree.expandRow(pos);
				pos = expandTree(tree, model, node, pathtree, pos);
				continue;
			}

			if (node.toString().contains(pathtree.get(0))) {
				while (pathtree.size() != 0) {
					int index = model.getIndexOfChild(parent, node);
					pos = ++pos + index;
					tree.expandRow(pos);
					pathtree.remove(0);
					if (pathtree.size() > 0)
						pos = expandTree(tree, model, node, pathtree, pos);
				}
				break;
			}
		}

		tree.setSelectionRow(pos);
		return pos;
	}

	public DirectoryBrowser(Frame f, boolean modal, String path, String name, Image icon) {
		super(f, modal);
		this.setLayout(new MigLayout("", "[grow]", "[grow][]"));

		this.setTitle("Select " + name + " folder");
		this.setIconImage(icon);
		this.setResizable(true);

		List<String> pathtree = new ArrayList<String>();
		pathList(path, pathtree);

		int rowHeight = 20;
		tree = new JTree();
		tree.setRowHeight(rowHeight);
		tree.setVisibleRowCount(15);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Desktop");
		File[] ff = File.listRoots();
		for (File dir : ff) {
			FileTreeNode dn = new FileTreeNode(dir);
			dn.setUserObject(dir.getAbsoluteFile());
			root.add(dn);
		}
		final DefaultTreeModel model = new DefaultTreeModel(root);
		tree.setModel(model);

		tree.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				TreePath tp = event.getPath();
				Object o = tp.getLastPathComponent();
				if (o instanceof FileTreeNode) {
					FileTreeNode dn = (FileTreeNode) o;
					scanChildren(model, dn);
					selected = dn.file;
				} else
					selected = null;
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				Object o = tp.getLastPathComponent();
				if (o instanceof FileTreeNode) {
					FileTreeNode dn = (FileTreeNode) o;
					scanChildren(model, dn);
					selected = dn.file;
				} else
					selected = null;
			}
		});
		int pos = expandTree(tree, model, model.getRoot(), pathtree, 0);

		scroll = new JScrollPane(tree);
		scroll.setViewportView(tree);

		scroll.getViewport().scrollRectToVisible(new Rectangle(0, pos * rowHeight, 0, 0));
		this.add(scroll, "cell 0 0,grow");

		JButton bt1 = new JButton("OK");
		bt1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		this.add(bt1, "flowx,cell 0 1,alignx right");

		JButton bt2 = new JButton("Cancel");
		bt2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selected = null;
				setVisible(false);
			}
		});
		this.add(bt2, "cell 0 1,alignx right");

		this.setMinimumSize(new Dimension(300, 300));
		this.pack();

		Point p = f.getLocation();
		this.setLocation(p.x + (f.getWidth() - this.getWidth()) / 2, p.y + (f.getHeight() - this.getHeight()) / 2);
	}

	public File getSelected() {
		return selected;
	}

	public static File showDirectoryChooser(Frame f, String path, String name, URL icon) {
		DirectoryBrowser dc = new DirectoryBrowser(f, true, path, name, Toolkit.getDefaultToolkit().getImage(icon));
		dc.setVisible(true);
		return dc.selected;
	}

	private void scanChildren(DefaultTreeModel model, FileTreeNode n) {
		if (n.isScan)
			return;
		File[] fs = n.file.listFiles();
		if (fs == null)
			return;
		for (File ff : fs) {
			if (ff.isDirectory()) {
				model.insertNodeInto(new FileTreeNode(ff), n, n.getChildCount());
			}
		}
		n.isScan = true;
	}

	private class FileTreeNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 1L;
		private File file;
		private boolean isScan = false;

		public FileTreeNode(File f) {
			super(f.getName());
			file = f;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}
	}

	/*
	 * JButton bt0=new JButton("Create Dir"); bt0.setFont(font);
	 * bt0.addActionListener(new ActionListener(){
	 * 
	 * @Override public void actionPerformed(ActionEvent arg0) {
	 * if(selectedFile==null)return; String name=JOptionPane.showInputDialog( tree,
	 * "Input the name of a new directory."); if(name==null||name.isEmpty())return;
	 * String path=selectedFile.getAbsolutePath()+sep+name; File ff=new File(path);
	 * if(ff.exists()){ JOptionPane.showMessageDialog( tree,
	 * "The directory exists. ","", JOptionPane.WARNING_MESSAGE); return; }
	 * if(ff.mkdir()){ DefaultTreeModel mm=(DefaultTreeModel)tree.getModel();
	 * DirectoryTreeNode dn=new DirectoryTreeNode(ff); TreePath
	 * tp=tree.getSelectionPath(); DirectoryTreeNode pn=
	 * (DirectoryTreeNode)tp.getLastPathComponent(); mm.insertNodeInto(dn, pn,
	 * pn.getChildCount()); selectedFile=ff; final TreePath tx=new
	 * TreePath(mm.getPathToRoot(dn)); Runnable r=new Runnable(){ public void run(){
	 * tree.setSelectionPath(tx); tree.expandPath(tx); } };
	 * javax.swing.SwingUtilities.invokeLater(r); }else{
	 * JOptionPane.showMessageDialog( tree,
	 * "It failed in making the directory. ","", JOptionPane.WARNING_MESSAGE); } }
	 * }); tool.add(bt0);
	 */
}
