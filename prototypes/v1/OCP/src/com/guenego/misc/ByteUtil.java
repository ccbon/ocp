package com.guenego.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtil {

	public static byte[] sub(byte[] input, int start) throws Exception {
		if (start >= input.length) {
			return null;
		}
		byte[] result = new byte[input.length - start];
		for (int i = start; i < input.length; i++) {
			result[i - start] = input[i];
		}
		return result;
	}

	public static byte[] concat(byte[]... input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < input.length; i++) {
			if (input[i] != null) {
				baos.write(input[i]);
			}
		}
		byte[] result = baos.toByteArray();
		baos.close();
		return result;
	}

}
