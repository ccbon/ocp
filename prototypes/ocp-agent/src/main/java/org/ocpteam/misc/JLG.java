package org.ocpteam.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class JLG {

	public static String NL;
	private static String[] base16 = null;

	static {
		NL = System.getProperty("line.separator");
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static void println(String string) {
		String s = string
				.replaceAll("\n", System.getProperty("line.separator"));
		System.out.println(s);
	}

	public static int random(int size) {
		return (int) Math.floor((Math.random() * size));
	}

	public static String bytesToHex(byte[] input) {
		if (base16 == null) {
			base16 = new String[256];
			for (int i = 0; i < 256; i++) {
				base16[i] = String.format("%1$02x", i);
			}
		}
		if (input == null) {
			return null;
		}
		StringBuffer result = new StringBuffer(input.length * 2);
		for (byte b : input) {
			result.append(base16[b & 0xff]);
		}
		return result.toString();
	}

	public static byte[] hexToBytes(String string) throws Exception {
		try {
			if (string.length() % 2 != 0) {
				throw new Exception("cannot convert to bytes (length="
						+ string.length() + "): " + string);
			}
			byte[] result = new byte[string.length() / 2];
			for (int i = 0; i < string.length(); i += 2) {
				result[i / 2] = (byte) Integer.parseInt(
						string.substring(i, i + 2), 16);
			}
			return result;
		} catch (Exception e) {
			LOG.debug("string = " + string);
			throw e;
		}
	}

	public static byte[] serialize(Serializable object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] response = baos.toByteArray();
		oos.close();
		baos.close();
		return response;
	}

	public static Serializable deserialize(byte[] input) throws Exception {
		if (input == null) {
			throw new Exception("input is null");
		}
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				input));
		Object obj = in.readObject();
		in.close();
		if (obj == null) {
			throw new Exception("Cannot deserialize");
		}
		return (Serializable) obj;
	}

	public static PublicKey getPublicKey(byte[] publicKey, String algo)
			throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algo);
		if (algo.equals("DSA")) {
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			return pubKey;
		} else {
			throw new Exception("algo" + algo + " not implemented...");
		}
	}

	public static String sha1(byte[] input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return (new Id(md.digest(input))).toString();
	}

	public static String input(String string) {
		System.out.print(string);
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			return s;
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}

	public static String join(String delimiter, Object... object) {
		String result = object[0].toString();
		for (int i = 1; i < object.length; i++) {
			result += delimiter + object[i].toString();
		}
		return result;
	}

	public static void rm(String path) {
		LOG.debug("removing " + path);
		rm(new File(path));
	}

	public static void rm(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				rm(child);
			}
		}
		LOG.debug("About to delete " + file.getAbsolutePath());
		file.delete();
	}

	public static void mkdir(String path) throws Exception {
		File dir = new File(path);
		mkdir(dir);
	}

	public static void mkdir(File dir) throws Exception {
		dir.mkdirs();
	}

	public static void setFile(String file, String content) throws Exception {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(content);
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
		}
	}

	public static void setBinaryFile(File file, byte[] content)
			throws Exception {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file, false);
			out.write(content);
			out.flush();
		} finally {
			out.close();
		}
	}

	public static byte[] getBinaryFile(File file) throws Exception {
		return FileUtils.readFileToByteArray(file);
	}

	public static boolean isFile(String path) {
		File file = new File(path);
		return file.isFile();
	}

	public static void storeConfig(Properties p, String file) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(file));
			p.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void saveProperties(File file, Properties p) throws Exception {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
				file));
		try {
			writeProperties(out, p);
		} finally {
			out.close();
		}

	}

	public static void writeProperties(OutputStreamWriter out, Properties p)
			throws Exception {
		Set<Object> keys = p.keySet();
		String[] array = keys.toArray(new String[keys.size()]);
		Arrays.sort(array);
		for (String key : array) {
			out.write(StringEscapeUtils.escapeJava(key + "="
					+ p.getProperty(key))
					+ JLG.NL);
		}
	}

	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.equals("");
	}

	public static String basename(String path) {
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		String[] a = path.split("/");
		return a[a.length - 1];
	}

	public static File createTempDirectory(String name) throws IOException {
		final File temp;

		temp = File.createTempFile(name, Long.toString(System.nanoTime()));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

	public static Properties loadConfig(String filename) throws Exception {
		Properties p = new Properties();
		File file = new File(filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			p.load(fis);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return p;
	}

	public static Properties extractProperties(Properties config, String prefix) {
		String s = prefix + ".";
		Properties p = new Properties();
		Iterator<String> it = config.stringPropertyNames().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.startsWith(s)) {
				String subkey = key.substring(s.length());
				p.setProperty(subkey, config.getProperty(key));
			}
		}
		return p;
	}

	public static String propertiesToString(Properties p) {
		String result = JLG.NL;
		Iterator<String> it = p.stringPropertyNames().iterator();
		while (it.hasNext()) {
			String key = it.next();
			result += key + "=" + p.getProperty(key) + JLG.NL;
		}
		return result;
	}

	public static void showActiveThreads() {
		while (true) {
			LOG.debug("active threads:");
			ThreadGroup tg = Thread.currentThread().getThreadGroup();
			Thread[] list = new Thread[tg.activeCount()];
			tg.enumerate(list);
			for (Thread t : list) {
				LOG.debug("running thread: " + t.getName());
			}

			ThreadGroup[] glist = new ThreadGroup[tg.activeGroupCount()];
			tg.enumerate(glist);
			for (ThreadGroup t : glist) {
				LOG.debug("running threadgroup: " + t.getName());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (Thread.activeCount() == 1) {
				break;
			}
		}
	}

	public static String dirname(String dir) {
		return new File(dir).getParentFile().getPath();
	}
}
