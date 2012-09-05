package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.LocalInventory;

import org.spout.api.inventory.Inventory;

public class SpoutInventory extends LocalInventory {
	Inventory inv;

	public SpoutInventory(Inventory inv) {
		this.inv = inv;
	}
	
	
}
