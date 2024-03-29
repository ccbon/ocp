package org.ocpteam.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class JLG {

	public static String NL;
	private static String[] base16 = null;

	static {
		NL = System.getProperty("line.separator");
	}
	private static boolean sDebug = false;

	public static void debug_on() {
		sDebug = true;
	}

	public static void debug_off() {
		sDebug = false;
	}

	public static boolean getDebugStatus() {
		return sDebug;
	}

	public static void debug(String input) {
		if (sDebug) {

			Throwable t = new Throwable();
			StackTraceElement ste = t.getStackTrace()[1];

			String sPrefix = "DEBUG [T=" + Thread.currentThread().getName()
					+ "] (" + ste.getClassName() + ".java:"
					+ ste.getLineNumber() + ") : ";
			System.out.println(sPrefix + input);
		}
	}

	public static void error(Exception e) {
		System.out.println("ERROR: " + e.getMessage());
		e.printStackTrace();
	}

	public static void println(String string) {
		String s = string
				.replaceAll("\n", System.getProperty("line.separator"));
		System.out.println(s);
	}

	public static int random(int size) {
		return (int) Math.round((Math.random() * size));
	}

	public static void error(String string) {
		System.out.println("ERROR: " + string);

	}

	public static void warn(Exception e) {
		System.out.println("WARNING: ");
		e.printStackTrace(System.out);

	}

	public static void warn(String string) {
		System.out.println("WARNING: " + string);

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
			JLG.debug("string = " + string);
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
			JLG.error(e);
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
		JLG.debug("removing " + path);
		rm(new File(path));
	}

	public static void rm(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				rm(child);
			}
		}
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
			p.store(out, "no comment");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
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

}
