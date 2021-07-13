package com.franek.meteor_tweaks.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;

public class Container {
	
	//public boolean isdouble;
	public final ItemStack[] ITEMS;
	public final Vec3i pos;
	
	public Container(Type type , Vec3i pos){
		this.pos = pos;
		this.ITEMS = new ItemStack[type.slots];
	}
	public Container(int slots , Vec3i pos){
		this.pos = pos;
		this.ITEMS = new ItemStack[slots];
	}
	
	
	public enum Type{
		SingleChest(27),
		DoubleChest(27*2);
		
		private final int slots;
		
		Type(int slots){
			this.slots = slots;
		}
	}
	
	
	//region coordsutils
	public static Container getContainerfromVec(Vec3i pos, Container[][][][] containers){
		try {
			return containers[pos.getY()][getCoordSysFromPos(pos).get][Math.abs(pos.getX())][Math.abs(pos.getZ())];
		}catch (IndexOutOfBoundsException e){
			return null;
		}
	}
	public static boolean setContainerfromVec(Vec3i pos, Container[][][][] containers, Container container){
		try {
			containers[pos.getY()][getCoordSysFromPos(pos).get][Math.abs(pos.getX())][Math.abs(pos.getZ())] = container;
			return true;
		}catch (IndexOutOfBoundsException e){
			return false;
		}
	}
	public enum ContainerCoordsSystem{
		plusXplusZ(0),
		plusXminusZ(1),
		minusXplusZ(2),
		minusXminuxZ(3);
		
		private final int get;
		
		ContainerCoordsSystem(int i) {
			get = i;
		}
	}
	public static ContainerCoordsSystem getCoordSysFromPos(Vec3i pos){
		if (pos.getX() >= 0){
			if (pos.getZ() >=0){
				return ContainerCoordsSystem.plusXplusZ;
			}else {
				return ContainerCoordsSystem.plusXminusZ;
			}
		}else {
			if (pos.getZ() >=0){
				return ContainerCoordsSystem.minusXplusZ;
			}else {
				return ContainerCoordsSystem.minusXminuxZ;
			}
		}
	}
	//endregion
}
