package com.guenego.ocp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.guenego.misc.JLG;

public class FileSystem {

	private Agent agent;
	private User user;
	private String path;

	public FileSystem(User user, Agent agent, String path) {
		this.user = user;
		this.agent = agent;
		this.path = path;
	}

	public void checkout() throws Exception {

		JLG.rm(path);
		JLG.mkdir(path);
		Pointer p = user.getRootPointer(agent);
		if (p == null) {
			return;
		}
		Tree rootTree = (Tree) agent.get(user, p);
		checkout(rootTree, new File(this.path));
	}

	public void checkout(TreeEntry te, File parentDir) throws Exception {
		Pointer p = te.getPointer();
		if (te.isTree()) {
			File dirFile = new File(parentDir, te.getName());
			dirFile.mkdir();

			Tree tree = (Tree) agent.get(user, p);
			checkout(tree, dirFile);
		} else if (te.isFile()) {
			File subFile = new File(parentDir, te.getName());
			byte[] content = (byte[]) agent.getBytes(user, p);
			setBinaryFile(subFile, content);
		}

	}

	private void checkout(Tree rootTree, File file) throws Exception {
		Iterator<TreeEntry> it = rootTree.getEntries().iterator();
		while (it.hasNext()) {
			TreeEntry te = it.next();
			checkout(te, file);
		}

	}

	private void setBinaryFile(File subFile, byte[] content) throws Exception {
		OutputStream out = null;
		try {
			out = new FileOutputStream(subFile);
			if (content != null) {
				out.write(content);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
		}
	}

	public void commit() throws Exception {
		// TODO: mettre tout les fichiers dans le cloud... de facon recursive
		File file = new File(path);
		if (!file.isDirectory()) {
			throw new Exception("must be a directory:" + path);
		}
		Pointer p = commit(file);
		user.setRootPointer(agent, p);
	}

	private Pointer commit(File file) throws Exception {
		Pointer result = null;
		if (file.isDirectory()) {
			Tree tree = new Tree();
			for (File child : file.listFiles()) {
				Pointer p = commit(child);
				if (child.isDirectory()) {
					tree.addTree(child.getName(), p);
				} else {
					tree.addFile(child.getName(), p);
				}
			}

			result = agent.set(user, tree);
		} else { // file
			byte[] content = JLG.getBinaryFile(file);
			result = agent.set(user, content);
		}
		return result;
	}

	public void commitFile(String path, File file) throws Exception {
		JLG.debug("path = " + path);
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		Pointer p = commit(file);
		Tree tree = trees[trees.length - 1];
		if (file.isDirectory()) {
			tree.addTree(file.getName(), p);
		} else {
			tree.addFile(file.getName(), p);
		}
		p = agent.set(user, tree);
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = agent.set(user, tree);
		}
		user.setRootPointer(agent, p);
	}

	private Tree[] getTreeStack(String[] dirnames) throws Exception {

		Tree[] treeStack = new Tree[dirnames.length];
		Pointer p = user.getRootPointer(agent);
		Tree tree = null;
		if (p == null) {
			tree = new Tree();
		} else {
			tree = (Tree) agent.get(user, p);
		}
		treeStack[0] = tree;
		for (int i = 1; i < dirnames.length; i++) {
			String dirname = dirnames[i];
			TreeEntry te = tree.getEntry(dirname);
			if (te == null || te.isFile()) {
				tree = new Tree();

			} else {
				p = te.getPointer();
				tree = (Tree) agent.get(user, p);

			}
			treeStack[i] = tree;
		}
		return treeStack;
	}

	public void deleteFile(String path, String name) throws Exception {
		JLG.debug("path = " + path);
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		
		Tree tree = trees[trees.length - 1];
		tree.removeEntry(name);
		Pointer p = agent.set(user, tree);
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = agent.set(user, tree);
		}
		user.setRootPointer(agent, p);
		
	}

	public void renameFile(String path, String oldName,
			String newName) throws Exception {
		JLG.debug("path = " + path);
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		
		Tree tree = trees[trees.length - 1];
		tree.renameEntry(oldName, newName);
		Pointer p = agent.set(user, tree);
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = agent.set(user, tree);
		}
		user.setRootPointer(agent, p);
		
	}

}
