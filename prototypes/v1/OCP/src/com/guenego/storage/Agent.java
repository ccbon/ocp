package com.guenego.storage;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.guenego.misc.Id;
import com.guenego.misc.JLG;
import com.guenego.ocp.Client;
import com.guenego.ocp.Server;
import com.guenego.ocp.Storage;

public abstract class Agent {

	public String name;

	public KeyPair keyPair;
	protected SecretKey secretKey;
	protected Cipher cipher;
	public String signatureAlgorithm;

	public Storage storage;

	public Properties p;
	public Properties network;

	public Client client;
	public Server server;

	protected SecretKeyFactory userSecretKeyFactory;
	protected Cipher userCipher;
	protected PBEParameterSpec userParamSpec;
	protected byte backupNbr;

	public Agent() {
	}

	public void loadConfig() throws Exception {
		if (!isConfigFilePresent()) {
			throw new Exception("Config file is not found. Expected Path: "
					+ getConfigFile().getAbsolutePath());
		}
		p = new Properties();
		p.load(new FileInputStream(getConfigFile()));
		readConfig();
	}

	public abstract File getConfigFile();

	public void loadConfig(Properties properties) throws Exception {
		p = properties;
		readConfig();
	}

	protected abstract void readConfig() throws Exception;

	public abstract void start() throws Exception;

	protected void attach() throws Exception {
		storage.attach();
	}

	public Id generateId() throws Exception {
		MessageDigest md = MessageDigest.getInstance(network.getProperty(
				"hash", "SHA-1"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] input = new byte[200];
		random.nextBytes(input);
		return new Id(md.digest(input));
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(network
				.getProperty("PKAlgo", "DSA"));
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.initialize(1024, random);
		return keyGen.generateKeyPair();
	}

	public boolean isFirstAgent() {
		if (p == null) {
			JLG.debug("p is null");
		}
		String s = p.getProperty("server", "no");
		return s.equalsIgnoreCase("yes")
				&& p.getProperty("server.isFirstAgent", "no").equalsIgnoreCase(
						"yes");
	}

	public abstract void stop();

	public void setNetworkProperties(Properties network) {
		this.network = network;
	}

	public Id hash(byte[] input) throws Exception {
		MessageDigest md = MessageDigest.getInstance(network.getProperty(
				"hash", "SHA-1"));
		return new Id(md.digest(input));
	}

	public byte[] crypt(String string) throws Exception, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(string.getBytes());
	}

	public String decrypt(byte[] ciphertext) throws Exception,
			BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(ciphertext));
	}

	public byte[] ucrypt(String password, String string) throws Exception,
			BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);

		userCipher.init(Cipher.ENCRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(string.getBytes());
	}

	public String udecrypt(String password, byte[] ciphertext)
			throws Exception, BadPaddingException {
		SecretKey secretKey = generateSecretKey(password);
		userCipher.init(Cipher.DECRYPT_MODE, secretKey, userParamSpec);
		return new String(userCipher.doFinal(ciphertext));
	}

	private SecretKey generateSecretKey(String password) throws Exception {
		return userSecretKeyFactory.generateSecret(new PBEKeySpec(password
				.toCharArray()));
	}

	public abstract boolean isConfigFilePresent();

	public abstract boolean allowsUserCreation();

	public abstract User login(String login, String password) throws Exception;

	public abstract void checkout(User user, String localDir) throws Exception;

	public abstract void commit(User user, String localDir) throws Exception;

	public abstract void mkdir(User user, String existingParentDir, String newDir) throws Exception;

	public abstract void rm(User user, String existingParentDir, String name) throws Exception;

	public abstract void rename(User user, String existingParentDir,
			String oldName, String newName) throws Exception;

	public abstract FileInterface getDir(User user, String dir) throws Exception;

}
