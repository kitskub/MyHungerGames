package com.randude14.hungergames.games;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventorySave {
	private ItemStack[] contents;
	private ItemStack[] armorContents;

	public InventorySave(Player player) {
		contents = player.getInventory().getContents();
		armorContents = player.getInventory().getArmorContents();
	}

	public void loadInventoryTo(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armorContents);
	}

}
