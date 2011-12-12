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

	private void checkout(Tree rootTree, File file) throws Exception {
		Iterator<String> it = rootTree.getEntries().keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			TreeEntry te = rootTree.getEntries().get(name);
			Pointer p = te.getPointer();
			if (te.isTree()) {
				File dirFile = new File(file, name);
				dirFile.mkdir();
				
				Tree tree = (Tree) agent.get(user, p);
				checkout(tree, dirFile);
			} else if (te.isFile()){
				File subFile = new File(file, name);
				byte[] content = (byte[]) agent.getBytes(user, p);
				setBinaryFile(subFile, content);
			}
		}
		
	}

	private void setBinaryFile(File subFile, byte[] content) throws Exception {
		OutputStream out = null;
		try {
			out = new FileOutputStream(subFile);
			out.write(content);
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


}
