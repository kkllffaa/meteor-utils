package com.franek.meteor_tweaks.utils;

import minegame159.meteorclient.utils.misc.ISerializable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

public class Container implements ISerializable<Container> {
	
	//public boolean isdouble;
	public final ItemStack[] ITEMS;
	public final Type type;
	
	public Container(Type type){
		if (type == null){
			this.ITEMS = null;
			this.type = null;
			return;
		}
		this.ITEMS = new ItemStack[type.slots];
		this.type = type;
	}
	
	
	public enum Type{
		SingleChest(27, Items.CHEST),
		DoubleChest(27*2, Items.CHEST);
		
		public final int slots;
		public final Item item;
		
		public static Type fromslots(int slots){
			return slots <= Container.Type.SingleChest.slots ? Container.Type.SingleChest :
					slots <= Container.Type.DoubleChest.slots ? Container.Type.DoubleChest : null;
		}
		
		public int rows(){
			return MathUtils.roundToBigger(slots/9.);
		}
		
		
		
		Type(int slots, Item item){
			this.slots = slots;
			this.item = item;
		}
	}
	
	
	
	@Override
	public NbtCompound toTag() {
		return null;
	}
	
	@Override
	public Container fromTag(NbtCompound tag) {
		
		return null;
	}
}
