package org.ocpteam.serializable;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Structure;

public class UserPublicInfo extends User implements IStructurable {

	private static final long serialVersionUID = 1L;
	private PublicKey publicKey;

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = super.toStructure();
		result.rename(getClass());
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		result.setByteArrayField("publicKey", x509EncodedKeySpec.getEncoded());
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		super.fromStructure(s);
		byte[] publicKeyEncoded = s.getBin("publicKey");
		if (publicKeyEncoded != null) {
			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					s.getBin("publicKey"));
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			setPublicKey(publicKey);
		}
	}
}
