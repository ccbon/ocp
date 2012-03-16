package org.ocpteam.protocol.ocp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.FileSystem;
import org.ocpteam.core.IContainer;
import org.ocpteam.layer.rsp.FileInterface;
import org.ocpteam.misc.JLG;


public class OCPFileSystem implements FileSystem {

	private OCPAgent agent;
	private OCPUser user;
	protected DataSource ds;

	public OCPFileSystem(OCPUser user, OCPAgent agent, String path) {
		this.user = user;
		this.agent = agent;
	}

	public OCPFileSystem(OCPUser user, OCPAgent agent) {
		this.user = user;
		this.agent = agent;
	}

	private void checkout(TreeEntry te, File parentDir) throws Exception {
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

	private Pointer commit(File file) throws Exception {
		JLG.debug("about to commit " + file.getName());
		Pointer result = null;
		if (file.isDirectory()) {
			Tree tree = new Tree();
			for (File child : file.listFiles()) {
				JLG.debug("child: " + child.getName());
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

	public void renameFile(String path, String oldName, String newName)
			throws Exception {
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

	public Tree getTree(String path) throws Exception {
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}

		Pointer p = user.getRootPointer(agent);
		if (p == null) {
			return null;
		}
		Tree tree = (Tree) agent.get(user, p);

		for (int i = 1; i < dirnames.length; i++) {
			String dirname = dirnames[i];
			TreeEntry te = tree.getEntry(dirname);
			if (te == null || te.isFile()) {
				return null;
			}
			p = te.getPointer();
			tree = (Tree) agent.get(user, p);
		}
		return tree;
	}

	public void createNewDir(String path, String directoryNew) throws Exception {
		JLG.debug("path = " + path);
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		Pointer p = agent.set(user, new Tree());
		Tree tree = trees[trees.length - 1];
		tree.addTree(directoryNew, p);
		p = agent.set(user, tree);
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = agent.set(user, tree);
		}
		user.setRootPointer(agent, p);
	}

	@Override
	public void checkoutAll(String localDir) throws Exception {
		JLG.rm(localDir);
		JLG.mkdir(localDir);
		Pointer p = user.getRootPointer(agent);
		if (p == null) {
			return;
		}
		Tree rootTree = (Tree) agent.get(user, p);
		checkout(rootTree, new File(localDir));

	}

	@Override
	public void commitAll(String localDir) throws Exception {
		File file = new File(localDir);
		if (!file.isDirectory()) {
			throw new Exception("must be a directory:" + localDir);
		}
		Pointer p = commit(file);
		user.setRootPointer(agent, p);
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename, File localDir)
			throws Exception {
		TreeEntry te = getTree(remoteDir).getEntry(remoteFilename);
		checkout(te, localDir);
		
	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		JLG.debug("path = " + remoteDir);
		String[] dirnames = remoteDir.split("/");
		if (remoteDir.equals("/")) {
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

	@Override
	public FileInterface getFile(String dir) throws Exception {
		return getTree(dir);
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		createNewDir(existingParentDir, newDir);
		
	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		deleteFile(existingParentDir, name);
	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		renameFile(existingParentDir, oldName, newName);
	}

	@Override
	public String getDefaultLocalDir() {
		return user.getDefaultLocalDir();
	}

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;
	}

}
