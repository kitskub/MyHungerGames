package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.LocalInventory;

import org.spout.api.inventory.Inventory;

public class SpoutInventory implements LocalInventory {
	Inventory inv;

	public SpoutInventory(Inventory inv) {
		this.inv = inv;
	}

	public ItemStack[] getContents() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean setContents(ItemStack[] contents) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setItem(int index, ItemStack stack) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public ItemStack[] addItem(ItemStack... stack) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	
}
