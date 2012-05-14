package org.ocpteam.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.ocpteam.component.AddressDataSource;
import org.ocpteam.component.DSContainer;
import org.ocpteam.entity.Address;
import org.ocpteam.entity.AddressUser;
import org.ocpteam.entity.SecureUser;
import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.interfaces.ISecurity;
import org.ocpteam.interfaces.IUser;
import org.ocpteam.misc.JLG;

/**
 * File System Data Model with big file support.
 * 
 */
public class BFSDataModel extends DSContainer<AddressDataSource> implements
		IFileSystem {

	private IAddressMap map;

	@Override
	public void init() throws Exception {
		super.init();
		map = ds().getComponent(IAddressMap.class);
	}

	public Address getRootAddress() throws Exception {
		String rootString = "";
		if (ds().getContext().hasUser()) {
			IUser user = ds().getContext().getUser();
			if (user instanceof AddressUser) {
				return ((AddressUser) user).getRootAddress();
			} else {
				return new Address(ds().md.hash(user.getUsername().getBytes()));
			}
		}
		return new Address(ds().md.hash(rootString.getBytes()));
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
		byte[] root = get(getRootAddress());
		if (root == null) {
			return null;
		}
		return (Pointer) ds().serializer.deserialize(root);
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
			createLocalFile(p, new File(parentDir, te.getName()));
		}
	}

	private void createLocalFile(Pointer p, File file) throws Exception {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file, false);

			for (Address address : p.getAddresses()) {
				byte[] content = get(address);
				out.write(content);
				out.flush();
			}
		} finally {
			out.close();
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
		byte[] value = null;
		value = ds().serializer.serialize(p);
		put(getRootAddress(), value);
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
			result = set(ds().serializer.serialize(tree));
		} else { // file
			result = createRemoteFile(file);
		}
		return result;
	}

	/**
	 * Put a file on the cloud.
	 * 
	 * @param file
	 * @return a pointer to the file
	 * @throws Exception
	 */
	private Pointer createRemoteFile(File file) throws Exception {
		FileInputStream fis = null;
		Pointer pointer = new Pointer();
		try {
			byte[] buffer = new byte[50000];
			fis = new FileInputStream(file);
			int n = fis.read(buffer);
			while (n >= 0) {
				Address address = new Address(ds().md.hash(buffer));
				put(address, buffer);
				pointer.add(address);
				n = fis.read(buffer);
			}

		} finally { // always close input stream
			fis.close();
		}
		return pointer;
	}

	private void put(Address address, byte[] value) throws Exception {
		if (ds().usesComponent(ISecurity.class)) {
			ISecurity security = ds().getComponent(ISecurity.class);
			SecureUser secureUser = (SecureUser) ds().getContext().getUser();
			security.put(secureUser, address, value);
		} else {
			map.put(address, value);
		}
	}

	private Pointer set(byte[] value) throws Exception {
		Address address = new Address(ds().md.hash(value));
		put(address, value);
		return new Pointer(address);
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
		JLG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
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
		JLG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
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
		JLG.debug("existingParentDir = " + existingParentDir);
		String[] dirnames = existingParentDir.split("/");
		if (existingParentDir.equals("/")) {
			dirnames = new String[] { "" };
		}
		JLG.debug("dirnames.length = " + dirnames.length);
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

	private byte[] get(Pointer p) throws Exception {
		Address address = p.getAddresses().get(0);
		return get(address);
	}

	private byte[] get(Address address) throws Exception {
		if (ds().usesComponent(ISecurity.class)) {
			ISecurity security = ds().getComponent(ISecurity.class);
			SecureUser secureUser = (SecureUser) ds().getContext().getUser();
			return security.get(secureUser, address);
		}
		return map.get(address);
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
