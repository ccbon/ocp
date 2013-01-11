package org.ocpteam.component;

import java.io.File;

import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Pointer;
import org.ocpteam.serializable.Tree;
import org.ocpteam.serializable.TreeEntry;

public class AddressFSDataModel extends DSContainer<AddressDataSource> implements IFileSystem {

	private IAddressMap map;
	private Address rootAddress;

	@Override
	public void init() throws Exception {
		super.init();
		map = ds().getComponent(IAddressMap.class);
	}
	
	public Address getRootAddress() {
		if (rootAddress == null) {
			rootAddress = new Address(ds().md.hash("".getBytes()));
		}
		return rootAddress;
	}
	
	@Override
	public void checkoutAll(String localDir) throws Exception {
		JLG.rm(localDir);
		JLG.mkdir(localDir);
		Pointer p = getRootPointer();
		if (p == null) {
			return;
		}
		Tree rootTree = getTree(p);
		checkout(rootTree, new File(localDir));
	}
	
	private Pointer getRootPointer() throws Exception {
		byte[] root = map.get(getRootAddress());
		if (root == null) {
			return null;
		}
		return new Pointer(root);
	}

	private void checkout(Tree rootTree, File file) throws Exception {
		for (TreeEntry te : rootTree.getEntries()) {
			checkout(te, file);
		}
	}
	
	private void checkout(TreeEntry te, File parentDir) throws Exception {
		Pointer p = te.getPointer();
		if (te.isTree()) {
			File dirFile = new File(parentDir, te.getName());
			dirFile.mkdir();

			Tree tree = getTree(p);
			checkout(tree, dirFile);
		} else if (te.isFile()) {
			File subFile = new File(parentDir, te.getName());
			byte[] content = get(p);
			JLG.setBinaryFile(subFile, content);
		}
	}

	@Override
	public void commitAll(String localDir) throws Exception {
		File file = new File(localDir);
		if (!file.isDirectory()) {
			throw new Exception("must be a directory:" + localDir);
		}
		Pointer p = commit(file);
		setRootPointer(p);
	}

	private void setRootPointer(Pointer p) throws Exception {
		map.put(getRootAddress(), p.getBytes());
	}

	private Pointer commit(File file) throws Exception {
		LOG.debug("about to commit " + file.getName());
		Pointer result = null;
		if (file.isDirectory()) {
			Tree tree = new Tree();
			for (File child : file.listFiles()) {
				LOG.debug("child: " + child.getName());
				Pointer p = commit(child);
				if (child.isDirectory()) {
					tree.addTree(child.getName(), p);
				} else {
					tree.addFile(child.getName(), p);
				}
			}
			result = set(ds().serializer.serialize(tree));
		} else { // file
			byte[] content = JLG.getBinaryFile(file);
			result = set(content);
		}
		return result;
	}

	private byte[] get(Pointer p) throws Exception {
		return map.get(new Address(p.getBytes()));
	}

	private Pointer set(byte[] value) throws Exception {
		Address address = new Address(ds().md.hash(value));
		map.put(address, value);
		return new Pointer(address.getBytes());
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename, File localDir)
			throws Exception {
		TreeEntry te = getTree(remoteDir).getEntry(remoteFilename);
		checkout(te, localDir);
	}

	@Override
	public void commit(String remoteDir, File file) throws Exception {
		LOG.debug("path = " + remoteDir);
		String[] dirnames = remoteDir.split("/");
		if (remoteDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		LOG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		Pointer p = commit(file);
		Tree tree = trees[trees.length - 1];
		if (file.isDirectory()) {
			tree.addTree(file.getName(), p);
		} else {
			tree.addFile(file.getName(), p);
		}
		p = set(ds().serializer.serialize(tree));
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = set(ds().serializer.serialize(tree));
		}
		setRootPointer(p);
	}

	@Override
	public IFile getFile(String dir) throws Exception {
		return getTree(dir);
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		LOG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		LOG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);
		Pointer p = set(ds().serializer.serialize(new Tree()));
		Tree tree = trees[trees.length - 1];
		tree.addTree(newDir, p);
		p = set(ds().serializer.serialize(tree));
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = set(ds().serializer.serialize(tree));
		}
		setRootPointer(p);
	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		LOG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		LOG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);

		Tree tree = trees[trees.length - 1];
		tree.removeEntry(name);
		Pointer p = set(ds().serializer.serialize(tree));
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = set(ds().serializer.serialize(tree));
		}
		setRootPointer(p);
	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		LOG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		LOG.debug("dirnames.length = " + dirnames.length);
		Tree[] trees = getTreeStack(dirnames);

		Tree tree = trees[trees.length - 1];
		tree.renameEntry(oldName, newName);
		Pointer p = set(ds().serializer.serialize(tree));
		for (int i = dirnames.length - 2; i >= 0; i--) {
			tree = trees[i];
			tree.addTree(dirnames[i + 1], p);
			p = set(ds().serializer.serialize(tree));
		}
		setRootPointer(p);
	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}
	
	private Tree getTree(String path) throws Exception {
		String[] dirnames = path.split("/");
		if (path.equals("/")) {
			dirnames = new String[] { "" };
		}

		Pointer p = getRootPointer();
		if (p == null) {
			return null;
		}
		Tree tree = getTree(p);

		for (int i = 1; i < dirnames.length; i++) {
			String dirname = dirnames[i];
			TreeEntry te = tree.getEntry(dirname);
			if (te == null || te.isFile()) {
				return null;
			}
			p = te.getPointer();
			tree = getTree(p);
		}
		return tree;
	}
	
	private Tree getTree(Pointer p) throws Exception {
		return (Tree) ds().serializer.deserialize(get(p));
	}

	private Tree[] getTreeStack(String[] dirnames) throws Exception {

		Tree[] treeStack = new Tree[dirnames.length];
		Pointer p = getRootPointer();
		Tree tree = null;
		if (p == null) {
			tree = new Tree();
		} else {
			tree = getTree(p);
		}
		treeStack[0] = tree;
		for (int i = 1; i < dirnames.length; i++) {
			String dirname = dirnames[i];
			TreeEntry te = tree.getEntry(dirname);
			if (te == null || te.isFile()) {
				tree = new Tree();

			} else {
				p = te.getPointer();
				tree = getTree(p);
			}
			treeStack[i] = tree;
		}
		return treeStack;
	}

}
