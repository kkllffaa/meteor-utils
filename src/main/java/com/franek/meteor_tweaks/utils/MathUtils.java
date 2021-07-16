package com.franek.meteor_tweaks.utils;

public class MathUtils {
	public static int roundToBigger(double a){
		if(a == (int)a) return (int)a;
		if(a > 0){
			if((int)a > a){
				return (int)a;
			}else{
				return (int)a + 1;
			}
		}else{
			if((int)a < a){
				return (int)a;
			}else{
				return (int)a - 1;
			}
		}
	}
}
