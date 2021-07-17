package com.franek.meteor_tweaks.systems.chestmemory;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.utils.MathUtils;
import minegame159.meteorclient.utils.misc.ISerializable;
import minegame159.meteorclient.utils.misc.NbtUtils;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.Arrays;

public class ContainerC implements ISerializable<ContainerC> {
	
	public ItemC[] getITEMS() {
		return ITEMS;
	}
	
	public Type getType() {
		return type;
	}
	
	//public boolean isdouble;
	private ItemC[] ITEMS;
	private Type type;
	
	public ContainerC(NbtCompound tag) {
		fromTag(tag);
	}
	
	public ContainerC(Type type){
		this.ITEMS = new ItemC[type.slots];
		this.type = type;
	}
	
	public ItemStack[] asStack(){
		if (!(ITEMS.length > 0) || type == Type.Null) return new ItemStack[27];
		ItemStack[] stack = new ItemStack[ITEMS.length];
		for (int i = 0; i < ITEMS.length; i++) {
			if (ITEMS[i] == null) continue;
			stack[i] = ITEMS[i].getStack();
		}
		//if (Arrays.stream(stack).allMatch(ItemStack::isEmpty)) return new ItemStack[27];
		return stack;
	}
	
	
	public static boolean goodblock(Block block) {
		return block instanceof AbstractChestBlock && !(block instanceof EnderChestBlock);
	}
	
	public enum Type{
		SingleChest(27, Items.CHEST),
		DoubleChest(27*2, Items.CHEST),
		Null(0, Items.ARROW);
		
		public final int slots;
		public final Item item;
		
		public static Type fromslots(int slots){
			return slots <= 0 ? Null : slots <= SingleChest.slots ? SingleChest :
					slots <= DoubleChest.slots ? DoubleChest : Null;
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
		NbtCompound tag = new NbtCompound();
		tag.putInt("type",type.ordinal());
		
		NbtList listitems = new NbtList();
		for (int i = 0; i < ITEMS.length; i++) {
			//todo
			if (ITEMS[i] == null) continue;
			NbtCompound item = ITEMS[i].toTag();
			item.putInt("slot",i);
			listitems.add(item);
		}
		if (!listitems.isEmpty()) tag.put("items",listitems);
		else return null;
		return tag;
	}
	
	@Override
	public ContainerC fromTag(NbtCompound tag) {
		if (tag.contains("type") && tag.contains("items") && Type.values().length > tag.getInt("type") && tag.getInt("type") >= 0 && !tag.getList("items", 10).isEmpty()) {
			
			type = Type.values()[tag.getInt("type")];
			NbtList itemlist = tag.getList("items", 10);
			
			
			ITEMS = new ItemC[type.slots];
			
			//todo
			
			for (NbtElement item : itemlist) {
				
				if (!((NbtCompound) item).contains("slot")) continue;
				
				int slot = ((NbtCompound) item).getInt("slot");
				
				if (slot < type.slots && slot >= 0) ITEMS[slot] = new ItemC(((NbtCompound) item));
				
			}
		}else {
			type = Type.Null;
			ITEMS = new ItemC[27];
		}
		return this;
	}
}
