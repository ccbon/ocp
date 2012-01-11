package com.guenego.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

public class JLG {
	static {
		NL = System.getProperty("line.separator");
	}
	public static String NL;
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
					+ "] (" + ste.getFileName() + ":" + ste.getLineNumber()
					+ ") : ";
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
		if (input == null) {
			return null;
		}
		String result = "";
		for (int i = 0; i < input.length; i++) {
			result += String.format("%1$02x", input[i]);
		}
		if (result.length() % 2 != 0) {
			JLG.error("Cannot convert to hex, result = " + result);
			(new Exception()).printStackTrace();
			System.exit(1);
		}
		return result;
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

	public static String serialize(Serializable object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		String response = JLG.bytesToHex(baos.toByteArray());
		oos.close();
		baos.close();
		return response;
	}

	public static Serializable deserialize(String sInput) throws Exception {
		JLG.debug("sInput = " + sInput);
		JLG.debug("sInput.length() = " + sInput.length());
		byte[] input = JLG.hexToBytes(sInput);
		JLG.debug("input.length = " + input.length);
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
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			throw new Exception("file is too large. It cannot exceed "
					+ Integer.MAX_VALUE);
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
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



}
