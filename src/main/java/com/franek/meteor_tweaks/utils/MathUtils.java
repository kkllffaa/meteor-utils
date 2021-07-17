package com.franek.meteor_tweaks.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3i;

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
	public static Vec3i vecfromtag(NbtCompound tag) {
		return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}
}
