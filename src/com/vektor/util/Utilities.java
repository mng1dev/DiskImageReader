package com.vektor.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Utilities {
	protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static short UValue(byte b) {
		return (short) (b & 0xFF);
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[(j * 2)] = hexArray[(v >>> 4)];
			hexChars[(j * 2 + 1)] = hexArray[(v & 0xF)];
		}
		return new String(hexChars);
	}

	public static int readLittleEndianWord(byte[] bytes) {
		byte b1 = bytes[0];
		byte b2 = bytes[1];
		byte b3 = bytes[2];
		byte b4 = bytes[3];
		int s = 0;
		s |= b4 & 0xFF;
		s <<= 8;
		s |= b3 & 0xFF;
		s <<= 8;
		s |= b2 & 0xFF;
		s <<= 8;
		s |= b1 & 0xFF;
		return s;
	}

	public static short readBigEndianWord(byte[] buf) {
		ByteBuffer bb = ByteBuffer.wrap(buf);
		bb.order(ByteOrder.BIG_ENDIAN);
		if (bb.hasRemaining()) {
			short v = bb.getShort();
			return v;
		}
		return 0;
	}

	private static boolean checkPowerOfTwo(int number) {
		if (number <= 0) {
			throw new IllegalArgumentException("number: " + number);
		}
		return (number & number - 1) == 0;
	}
}
