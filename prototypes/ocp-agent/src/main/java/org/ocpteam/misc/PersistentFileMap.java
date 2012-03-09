package org.ocpteam.misc;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PersistentFileMap implements Map<byte[], byte[]> {

	private File dir;

	public static class PersistentMapEntry implements Map.Entry<byte[], byte[]> {

		private byte[] address;
		private byte[] content;

		public PersistentMapEntry(byte[] address, byte[] content) {
			this.address = address;
			this.content = content;
		}

		@Override
		public byte[] getKey() {
			return address;
		}

		@Override
		public byte[] getValue() {
			return content;
		}

		@Override
		public byte[] setValue(byte[] value) {
			content = value;
			return content;
		}

	}

	public PersistentFileMap() {
	}

	public void setRoot(String root) throws Exception {
		dir = new File(root);
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
	public boolean containsKey(Object key) {
		try {
			for (File child : dir.listFiles()) {
				byte[] address = JLG.hexToBytes(child.getName());
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
	public boolean containsValue(Object value) {
		try {
			for (File child : dir.listFiles()) {
				byte[] content = JLG.getBinaryFile(child);
				if (content.equals(value)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return false;
	}

	@Override
	public Set<java.util.Map.Entry<byte[], byte[]>> entrySet() {
		Set<java.util.Map.Entry<byte[], byte[]>> result = new HashSet<java.util.Map.Entry<byte[], byte[]>>();
		try {
			for (File child : dir.listFiles()) {
				byte[] address = JLG.hexToBytes(child.getName());
				byte[] content = JLG.getBinaryFile(child);
				PersistentMapEntry entry = new PersistentMapEntry(address,
						content);
				result.add(entry);
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public byte[] get(Object key) {
		try {
			File file = new File(dir, JLG.bytesToHex((byte[]) key));
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
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<byte[]> keySet() {
		Set<byte[]> result = new HashSet<byte[]>();
		try {
			for (File child : dir.listFiles()) {
				byte[] address = JLG.hexToBytes(child.getName());
				result.add(address);
			}
		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public byte[] put(byte[] key, byte[] value) {
		try {
			File file = new File(dir, JLG.bytesToHex(key));
			JLG.setBinaryFile(file, value);
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends byte[], ? extends byte[]> m) {
		Iterator<? extends byte[]> it = m.keySet().iterator();
		while (it.hasNext()) {
			byte[] address = (byte[]) it.next();
			byte[] content = m.get(address);
			put(address, content);
		}
	}

	@Override
	public byte[] remove(Object key) {
		try {
			File file = new File(dir, JLG.bytesToHex((byte[]) key));
			if (file.exists()) {
				byte[] content = JLG.getBinaryFile(file);
				file.delete();
				return content;
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public int size() {
		return dir.listFiles().length;
	}

	@Override
	public Collection<byte[]> values() {
		Collection<byte[]> result = new HashSet<byte[]>();
		try {
			for (File child : dir.listFiles()) {
				byte[] content = JLG.getBinaryFile(child);
				result.add(content);
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return result;
	}

}
