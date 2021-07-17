package com.franek.meteor_tweaks.systems.chestmemory;

import com.franek.meteor_tweaks.utils.MathUtils;
import minegame159.meteorclient.utils.misc.ISerializable;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.Objects;

public class ContainerC implements ISerializable<ContainerC> {
	
	public ItemC[] getITEMS() {
		switch (type) {
			case SingleChest, DoubleChest -> { return ITEMS; }
			case LINK -> {
				if (ChestMemory.get(doublepos) == null || ChestMemory.get(doublepos).type != Type.DoubleChest || ChestMemory.get(doublepos).getITEMS() == null || ChestMemory.get(doublepos).ITEMS.length <= 0) {
					return null;
				}else {
					return ChestMemory.get(doublepos).ITEMS;
				}
			}
			case Null -> { return null;	}
		}
		return null;
	}
	
	public Type getType() {
		return type;
	}
	
	private Vec3i doublepos;
	private ItemC[] ITEMS;
	private Type type;
	
	public ContainerC(NbtCompound tag) {
		fromTag(tag);
	}
	
	public ContainerC(Type type){
		this.ITEMS = new ItemC[type.slots];
		this.type = type;
	}
	
	public ContainerC(Vec3i linkedchestpos) {
		this.type = Type.LINK;
		this.doublepos = linkedchestpos;
	}
	
	public ItemStack[] asStack(){
		if (getITEMS() != null && !(getITEMS().length > 0)) {
			
			ItemStack[] stack = new ItemStack[getITEMS().length];
			
			for (int i = 0; i < getITEMS().length; i++) {
				if (getITEMS()[i] == null) continue;
				stack[i] = getITEMS()[i].getStack();
			}
			return stack;
			
		}else return new ItemStack[27];
	}
	
	
	public static boolean goodblock(Block block) {
		return block instanceof AbstractChestBlock && !(block instanceof EnderChestBlock);
	}
	
	public enum Type{
		SingleChest(27),
		DoubleChest(27*2),
		LINK(27*2),
		Null(0);
		
		public final int slots;
		
		public static Type fromslots(int slots){
			return slots <= 0 ? Null : slots <= SingleChest.slots ? SingleChest :
					slots <= DoubleChest.slots ? DoubleChest : Null;
		}
		
		public boolean isdouble() {
			return this == DoubleChest || this == LINK;
		}
		
		public boolean local() {
			return this == DoubleChest || this == SingleChest;
		}
		
		public int rows(){
			return MathUtils.roundToBigger(slots/9.);
		}
		
		Type(int slots){
			this.slots = slots;
		}
	}
	
	
	
	@Override
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		tag.putInt("type",type.ordinal());
		
		switch (type) {
			case SingleChest, DoubleChest -> {
				NbtList listitems = new NbtList();
				for (int i = 0; i < ITEMS.length; i++) {
					if (ITEMS[i] == null) continue;
					NbtCompound item = ITEMS[i].toTag();
					item.putInt("slot",i);
					listitems.add(item);
				}
				if (!listitems.isEmpty()) tag.put("items",listitems);
				else tag = null;
			}
			case LINK -> tag.put("doublepos", MathUtils.vectotag(doublepos));
			case Null -> tag = null;
		}
		

		return tag;
	}
	
	@Override
	public ContainerC fromTag(NbtCompound tag) {
		if (tag.contains("type") && Type.values().length > tag.getInt("type") && tag.getInt("type") >= 0) {
			type = Type.values()[tag.getInt("type")];
			switch (type) {
				case SingleChest, DoubleChest -> {
					if (tag.contains("items") && !tag.getList("items", 10).isEmpty()) {
						NbtList itemlist = tag.getList("items", 10);
						
						ITEMS = new ItemC[type.slots];
						
						
						for (NbtElement item : itemlist) {
							
							if (!((NbtCompound) item).contains("slot")) continue;
							
							int slot = ((NbtCompound) item).getInt("slot");
							
							if (slot < type.slots && slot >= 0) ITEMS[slot] = new ItemC(((NbtCompound) item));
							
						}
						if (Arrays.stream(ITEMS).allMatch(Objects::isNull)) type = Type.Null;
					}else type = Type.Null;
				}
				case LINK -> {
					if (tag.contains("doublepos")) {
						doublepos = MathUtils.vecfromtag(tag.getCompound("doublepos"));
					}else type = Type.Null;
				}
				case Null -> {}
			}
		} else type = Type.Null;
		ITEMS = new ItemC[type.slots];
		return this;
	}
}
