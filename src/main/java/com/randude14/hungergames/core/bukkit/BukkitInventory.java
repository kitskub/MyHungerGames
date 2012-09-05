package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.LocalInventory;

import org.bukkit.inventory.Inventory;

public class BukkitInventory implements LocalInventory {
	Inventory inv;

	public BukkitInventory(Inventory inv) {
		this.inv = inv;
	}

	public ItemStack[] getContents() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean setContents(ItemStack[] contents) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setItem(int index, ItemStack stack) {
		inv.setItem(index, BukkitUtil.convertItemStack(stack));
	}

	public ItemStack[] addItem(ItemStack... stack) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
