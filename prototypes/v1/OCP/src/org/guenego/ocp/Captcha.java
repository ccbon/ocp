package org.guenego.ocp;

import java.io.Serializable;
import java.security.PublicKey;
import java.security.Signature;

import org.guenego.misc.Id;
import org.guenego.misc.JLG;


public class Captcha implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String challengeObject;
	public long created;
	public byte[] cryptedAnswer;
	public Id contactId;
	public String signatureAlgo;
	public byte[] signature;

	public Captcha(OCPAgent agent) throws Exception {
		this.challengeObject = "the answer is :didounette";
		this.created = System.currentTimeMillis();
		cryptedAnswer = agent.crypt("didounette".getBytes());
		JLG.debug("cryptedAnswer = " + cryptedAnswer);
		JLG.debug("decryptedAnswer = " + new String(agent.decrypt(cryptedAnswer)));
		contactId = agent.id;
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
			throw new Exception("bad answer (" + answer + "!=" + clearAnswer + ")");
		}
	}
}
