package org.ocpteam.component;

import java.security.SecureRandom;

public class Random {
	public byte[] generate() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}
}
