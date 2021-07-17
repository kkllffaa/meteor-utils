package com.franek.meteor_tweaks.systems.chestmemory;

import minegame159.meteorclient.utils.misc.ISerializable;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemC implements ISerializable<ItemC> {
	private Item item;
	private int count;
	private boolean enchanted;
	private int demage;
	
	public Item getItem() {
		return item;
	}
	
	public ItemStack getStack(){
		ItemStack stack = new ItemStack(item, count);
		if (demage > 0) stack.setDamage(demage);
		if (enchanted) stack.addEnchantment(Enchantments.UNBREAKING,1);
		return stack;
	}
	
	public ItemC(Item item, int count, boolean enchanted, int demage){
		this.item = item;
		this.count = count;
		this.enchanted = enchanted;
		this.demage = demage;
	}
	
	public ItemC(Item item){this(item,1,false,0);}
	
	public ItemC(ItemStack stack){
		this(stack.getItem(),stack.getCount(),stack.hasEnchantments(),stack.getDamage());
	}
	
	public ItemC(NbtCompound nbt){
		fromTag(nbt);
	}

	
	@Override
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		tag.putString("item", Registry.ITEM.getId(item).toString());
		if (count > 1) tag.putInt("count",count);
		if (enchanted) tag.putBoolean("enchanted", true);
		if (demage > 0) tag.putInt("demage",demage);
		return tag;
	}
	
	@Override
	public ItemC fromTag(NbtCompound tag) {
		item = Registry.ITEM.get(new Identifier(tag.getString("item")));
		count = tag.contains("count") ? tag.getInt("count") : 1;
		enchanted = tag.contains("enchanted") && tag.getBoolean("enchanted");
		demage = tag.contains("demage") ? tag.getInt("demage") : 0;
		return this;
	}
}
