package com.randude14.hungergames.core.spout;

import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.Vector;
import com.randude14.hungergames.core.blocks.Chest;
import org.spout.vanilla.material.VanillaMaterials;

public  class SpoutChest extends Chest {

	public SpoutChest(SpoutWorld world, Vector pos) {
		super(VanillaMaterials.CHEST.getId(), world, pos);
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
