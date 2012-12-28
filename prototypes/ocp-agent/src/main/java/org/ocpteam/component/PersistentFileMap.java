package org.ocpteam.component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;

public class PersistentFileMap implements IPersistentMap {

	private File dir;

	@Override
	public void setURI(String uri) throws Exception {
		dir = new File(uri);
		JLG.mkdir(dir);
	}

	@Override
	public void clear() {
		try {
			JLG.rm(dir);
			JLG.mkdir(dir);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public boolean containsKey(Address key) {
		try {
			for (File child : dir.listFiles()) {
				Address address = new Address(JLG.hexToBytes(child.getName()));
				if (address.equals(key)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}
		return false;
	}

	@Override
	public byte[] get(Address address) {
		try {
			File file = new File(dir, JLG.bytesToHex(address.getBytes()));
			if (file.exists()) {
				byte[] content = JLG.getBinaryFile(file);
				return content;
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public Set<Address> keySet() {
		Set<Address> result = new HashSet<Address>();
		try {
			for (File child : dir.listFiles()) {
				Address address = new Address(JLG.hexToBytes(child.getName()));
				result.add(address);
			}
		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public void put(Address key, byte[] value) {
		try {
			File file = new File(dir, JLG.bytesToHex(key.getBytes()));
			JLG.setBinaryFile(file, value);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public void putAll(IDataStore datastore) throws Exception {
		for (Address address : datastore.keySet()) {
			put(address, datastore.get(address));
		}
	}

	@Override
	public void remove(Address key) {
		try {
			File file = new File(dir,
					JLG.bytesToHex(((Address) key).getBytes()));
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			JLG.error(e);
		}
	}
}
