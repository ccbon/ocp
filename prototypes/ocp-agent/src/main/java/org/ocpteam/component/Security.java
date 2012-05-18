package org.ocpteam.component;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.ocpteam.interfaces.IAddressMap;
import org.ocpteam.interfaces.ISecurity;
import org.ocpteam.interfaces.IUserBackup;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Content;
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.UserPublicInfo;

public class Security extends DSContainer<AddressDataSource> implements
		ISecurity {

	private SecretKeyFactory userSecretKeyFactory;
	private Cipher userCipher;
	private PBEParameterSpec userParamSpec;
	private IAddressMap map;
	private Signature sign;
	private Cipher cipher;

	@Override
	public void init() throws Exception {
		super.init();
		map = ds().getComponent(IAddressMap.class);
	}

	public void readNetworkConfig() throws Exception {
		userSecretKeyFactory = SecretKeyFactory.getInstance(ds().network
				.getProperty("user.cipher.algo", "PBEWithMD5AndDES"));
		userCipher = Cipher.getInstance(ds().network.getProperty(
				"user.cipher.algo", "PBEWithMD5AndDES"));

		cipher = Cipher.getInstance(ds().network
				.getProperty("user.crypt.algo", "AES"));

		// user cipher
		byte[] salt = { 1, 1, 1, 2, 2, 2, 3, 3 };
		int count = 20;
		userParamSpec = new PBEParameterSpec(salt, count);

		// signature
		sign = Signature.getInstance(ds().network.getProperty(
				"user.signature.algo", "SHA1withDSA"));

	}

	private SecretKey generateSecretKey(String password) throws Exception {
		return userSecretKeyFactory.generateSecret(new PBEKeySpec(password
				.toCharArray()));
	}

	@Override
	public void generateKeyPair(SecureUser secureUser) throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(secureUser
				.getKeyPairAlgo());
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.initialize(1024, random);
		secureUser.setKeyPair(keyGen.generateKeyPair());
	}

	@Override
	public void generateSecretKey(SecureUser secureUser) throws Exception {
		String algo = secureUser.getSecretKeyAlgo();
		if (algo.equals("AES")) {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			secureUser.setSecretKey(keyGen.generateKey());
		}
	}

	@Override
	public UserPublicInfo getPublicInfo(SecureUser secureUser) {
		UserPublicInfo upi = new UserPublicInfo();
		upi.setUsername(secureUser.getUsername());
		upi.setPublicKey(secureUser.getKeyPair().getPublic());
		return upi;
	}

	@Override
	public byte[] passwordCrypt(String password, byte[] value) throws Exception {
		SecretKey secretKey = generateSecretKey(password);
		userCipher.init(Cipher.ENCRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(value);
	}

	@Override
	public byte[] passwordDecrypt(String password, byte[] value)
			throws Exception {
		SecretKey secretKey = generateSecretKey(password);
		userCipher.init(Cipher.DECRYPT_MODE, secretKey, userParamSpec);
		return userCipher.doFinal(value);
	}

	@Override
	public byte[] crypt(SecureUser secureUser, byte[] value) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, secureUser.getSecretKey());
		return cipher.doFinal(value);
	}

	@Override
	public byte[] decrypt(SecureUser secureUser, byte[] value) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, secureUser.getSecretKey());
		return cipher.doFinal(value);
	}

	@Override
	public void putUPI(SecureUser secureUser) throws Exception {
		UserPublicInfo upi = getPublicInfo(secureUser);
		Address a = new Address(ds().md.hash((secureUser.getUsername()
				.getBytes())));
		byte[] value = ds().serializer.serialize(upi);
		byte[] signature = sign(secureUser, value);
		Content content = new Content(secureUser.getUsername(), value,
				signature);
		map.put(a, ds().serializer.serialize(content));
	}

	@Override
	public UserPublicInfo getUPI(String username) throws Exception {
		Address a = new Address(ds().md.hash((username.getBytes())));
		byte[] value = map.get(a);
		if (value == null) {
			throw new Exception("User not found");
		}
		Content content = (Content) ds().serializer.deserialize(value);
		UserPublicInfo upi = (UserPublicInfo) ds().serializer
				.deserialize(content.getValue());
		byte[] signature = content.getSignature();
		if (!verify(upi, content.getValue(), signature)) {
			throw new Exception("Bad signature.");
		}
		return upi;
	}

	@Override
	public byte[] sign(SecureUser secureUser, byte[] value) throws Exception {
		sign.initSign(secureUser.getKeyPair().getPrivate());
		sign.update(value);
		return sign.sign();
	}

	@Override
	public boolean verify(UserPublicInfo upi, byte[] value, byte[] signature)
			throws Exception {
		sign.initVerify(upi.getPublicKey());
		sign.update(value);
		return sign.verify(signature);
	}

	@Override
	public void put(SecureUser secureUser, Address address, byte[] value)
			throws Exception {
		byte[] crypted = crypt(secureUser, value);
		byte[] signature = sign(secureUser, crypted);
		Content content = new Content(secureUser.getUsername(), crypted,
				signature);
		byte[] s = ds().serializer.serialize(content);
		if (ds().usesComponent(IUserBackup.class)) {
			IUserBackup userBackup = ds().getComponent(IUserBackup.class);
			Address[] addresses = userBackup.getAddresses(secureUser, address);
			for (Address a : addresses) {
				map.put(a, s);
			}
		} else {
			map.put(address, s);
		}
	}

	@Override
	public byte[] get(SecureUser secureUser, Address address) throws Exception {
		byte[] value = null;
		
		if (ds().usesComponent(IUserBackup.class)) {
			IUserBackup userBackup = ds().getComponent(IUserBackup.class);
			Address[] addresses = userBackup.getAddresses(secureUser, address);
			for (Address a : addresses) {
				value = map.get(a);
				if (value != null) {
					break;
				}
			}
		} else {
			value = map.get(address);
		}
		
		if (value == null) {
			return null;
		}
		Content content = (Content) ds().serializer.deserialize(value);
		UserPublicInfo upi = getPublicInfo(secureUser);
		if (!verify(upi, content.getValue(), content.getSignature())) {
			throw new Exception("Bad signature.");
		}
		return decrypt(secureUser, content.getValue());
	}

	@Override
	public void putUser(SecureUser secureUser, String password)
			throws Exception {
		JLG.debug("secureUser.getUsername()=" + secureUser.getUsername());
		JLG.debug("password=" + password);
		Address address = getUserAddress(secureUser.getUsername(), password);
		byte[] value = ds().serializer.serialize(secureUser);
		value = passwordCrypt(password, value);
		byte[] signature = sign(secureUser, value);
		Content content = new Content(secureUser.getUsername(), value,
				signature);
		map.put(address, ds().serializer.serialize(content));
	}

	private Address getUserAddress(String username, String password) {
		return new Address(ds().md.hash((username + password).getBytes()));
	}

	@Override
	public SecureUser getUser(String username, String password)
			throws Exception {
		UserPublicInfo upi = getUPI(username);
		Address address = getUserAddress(username, password);
		byte[] value = map.get(address);
		if (value == null) {
			return null;
		}
		Content content = (Content) ds().serializer.deserialize(value);
		if (!verify(upi, content.getValue(), content.getSignature())) {
			throw new Exception("Bad signature.");
		}
		value = passwordDecrypt(password, content.getValue());
		return (SecureUser) ds().serializer.deserialize(value);
	}

}
