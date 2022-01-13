package com.kkllffaa.meteorutils.utils;

@SuppressWarnings("unused")
public class MyUtils {
	public static String yesno(boolean b) {
		return b ? "yes" : "no";
	}
	public static String printif(String s, boolean b) {
		return b ? s : "";
	}
	
	
	public static boolean isInt(String s) {
		return isInt(s, true);
	}
	public static boolean isUint(String s) {
		return isInt(s, false);
	}
	
	private static boolean isInt(String s, boolean negative) {
		if (s == null || s.isEmpty()) return false;
		try {
			if (negative && s.startsWith("-")) {
				Integer.parseInt(s.substring(1));
			}else {
				Integer.parseInt(s);
			}
			return true;
		}catch (NumberFormatException e) {return false;}
	}
	
	public static int getInt(String s) {
		if (s == null || s.isEmpty()) return 0;
		try {
			if (s.startsWith("-")) {
				return -Integer.parseInt(s.substring(1));
			}else {
				return Integer.parseInt(s);
			}
		}catch (NumberFormatException e) {return 0;}
	}
}
