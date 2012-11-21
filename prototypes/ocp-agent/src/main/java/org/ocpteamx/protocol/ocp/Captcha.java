package org.ocpteamx.protocol.ocp;

import java.security.PublicKey;
import java.security.Signature;

import org.ocpteam.interfaces.ICaptcha;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;

public class Captcha implements ICaptcha {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String challengeObject;
	public long created;
	public byte[] cryptedAnswer;
	public String contactId;
	public String signatureAlgo;
	public byte[] signature;

	public Captcha() {
	}

	public Captcha(OCPAgent agent) throws Exception {
		this.challengeObject = "the answer is :didounette";
		this.created = System.currentTimeMillis();
		cryptedAnswer = agent.crypt("didounette".getBytes());
		JLG.debug("cryptedAnswer = " + cryptedAnswer);
		JLG.debug("decryptedAnswer = "
				+ new String(agent.decrypt(cryptedAnswer)));
		contactId = agent.id.toString();
		JLG.debug("contactId = " + contactId);
		signatureAlgo = agent.signatureAlgorithm;
		Signature s = Signature.getInstance(signatureAlgo);
		s.initSign(agent.keyPair.getPrivate());
		s.update(challengeObject.toString().getBytes());
		s.update((created + "").getBytes());
		s.update(cryptedAnswer);
		s.update(contactId.toString().getBytes());
		s.update(signatureAlgo.getBytes());
		signature = s.sign();
	}

	@Override
	public String toString() {
		return "challengeObject = " + challengeObject;
	}

	public boolean checkSignature(OCPAgent agent) throws Exception {
		Signature s = Signature.getInstance(signatureAlgo);
		PublicKey pubKey = agent.keyPair.getPublic();
		s.initVerify(pubKey);
		s.update(challengeObject.toString().getBytes());
		s.update((created + "").getBytes());
		s.update(cryptedAnswer);
		s.update(contactId.toString().getBytes());
		s.update(signatureAlgo.getBytes());
		return s.verify(signature);

	}

	public void check(OCPAgent agent, String answer) throws Exception {
		// first check that I generated myself this captcha
		if (!checkSignature(agent)) {
			throw new Exception("problem while checking the signature");
		}
		String clearAnswer = new String(agent.decrypt(this.cryptedAnswer));
		if (!answer.equals(clearAnswer)) {
			throw new Exception("bad answer (" + answer + "!=" + clearAnswer
					+ ")");
		}
	}

	@Override
	public Structure toStructure() throws Exception {
		Structure result = new Structure(getClass());
		result.setStringField("challengeObject", challengeObject);
		result.setStringField("contactId", contactId);
		result.setStringField("signatureAlgo", signatureAlgo);
		result.setStringField("created", "" + created);
		result.setBinField("cryptedAnswer", cryptedAnswer);
		result.setBinField("signature", signature);
		return result;
	}

	@Override
	public void fromStructure(Structure s) throws Exception {
		challengeObject = s.getString("challengeObject");
		contactId = s.getString("contactId");
		signatureAlgo = s.getString("signatureAlgo");
		created = Long.parseLong(s.getString("created"));
		cryptedAnswer = s.getBin("cryptedAnswer");
		signature = s.getBin("signature");
	}
}
