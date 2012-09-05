package com.randude14.hungergames.core;

public interface LocalInventory {
	
	public ItemStack[] getContents();

	public boolean setContents(ItemStack[] contents);
	
	public void setItem(int index, ItemStack stack);
	
	public ItemStack[] addItem(ItemStack... stack);
}
