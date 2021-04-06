package com.loohp.limbo.utils;

public class CustomArrayUtils {
	
	public static int[] longArrayToIntArray(long[] numbers) {
		int[] intNumbers = new int[numbers.length];
		for(int i = 0; i < numbers.length; i++) {
		    intNumbers[i] = (int) numbers[i];
		}
		return intNumbers;
	}
	
	public static byte[] longArrayToByteArray(long[] numbers) {
		byte[] intNumbers = new byte[numbers.length];
		for(int i = 0; i < numbers.length; i++) {
		    intNumbers[i] = (byte) numbers[i];
		}
		return intNumbers;
	}

}
