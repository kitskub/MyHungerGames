package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.ItemStack;
import com.randude14.hungergames.core.LocalPlayerInventory;
import java.util.ArrayList;

import org.bukkit.inventory.PlayerInventory;

public class BukkitPlayerInventory extends LocalPlayerInventory {
	PlayerInventory inv;

	public BukkitPlayerInventory(PlayerInventory inv) {
		this.inv = inv;
	}

	@Override
	public ItemStack[] getContents() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (org.bukkit.inventory.ItemStack item : inv.getContents()) {
			list.add(BukkitUtil.convertItemStack(item));
		}
		return (ItemStack[]) list.toArray();
	}

	@Override
	public ItemStack[] getArmorContents() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (org.bukkit.inventory.ItemStack item : inv.getArmorContents()) {
			list.add(BukkitUtil.convertItemStack(item));
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
