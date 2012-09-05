package com.randude14.hungergames.core.spout;


import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.LocalPlayerInventory;

import java.util.ArrayList;

import org.spout.vanilla.inventory.player.PlayerInventory;

public class SpoutPlayerInventory extends LocalPlayerInventory {
	PlayerInventory inv;

	public SpoutPlayerInventory(PlayerInventory inv) {
		this.inv = inv;
	}
	
	@Override
	public ItemStack[] getContents() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (org.spout.api.inventory.ItemStack item : inv.getContents()) {
			list.add(SpoutUtil.convertItemStack(item));
		}
		return (ItemStack[]) list.toArray();
	}

	@Override
	public ItemStack[] getArmorContents() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (org.spout.api.inventory.ItemStack item : inv.getArmor().getContents()) {
			list.add(SpoutUtil.convertItemStack(item));
		}
		return (ItemStack[]) list.toArray();
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
