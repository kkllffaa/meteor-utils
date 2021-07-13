package com.franek.meteor_tweaks.utils;

import minegame159.meteorclient.utils.misc.ISerializable;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class Vector2i implements ISerializable<Vector2i>{
	
	public static final Vector2i ZERO = new Vector2i();
	
	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2i() {
		this(0, 0);
	}
	
	public Vector2i(Vector2i other) {
		this(other.x, other.y);
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		
		tag.putDouble("x", x);
		tag.putDouble("y", y);
		
		return tag;
	}
	
	@Override
	public Vector2i fromTag(NbtCompound tag) {
		x = tag.getInt("x");
		y = tag.getInt("y");
		
		return this;
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vector2i vector2i = (Vector2i) o;
		return Integer.compare(vector2i.x, x) == 0 &&
				Integer.compare(vector2i.y, y) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
}
