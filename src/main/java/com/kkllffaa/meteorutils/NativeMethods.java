package com.kkllffaa.meteorutils;

public class NativeMethods {
	static{
		System.load("C:/Users/Franek/source/repos/jnitest/x64/Release/jnitest.dll");
	}
	
	static native void ble(int ga);
	
	static native int ff();
	static native int addd(int a, int b);
}
