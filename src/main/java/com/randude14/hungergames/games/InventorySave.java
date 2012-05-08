package com.randude14.hungergames.games;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventorySave {
	private static final Map<Player, InventorySave> savedInventories = new HashMap<Player, InventorySave>();
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;

	private InventorySave(Player player) {
		contents = player.getInventory().getContents();
		armorContents = player.getInventory().getArmorContents();
	}

	private void loadInventoryTo(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armorContents);
	}
	
	public static void saveAndClearInventory(Player player){
	    savedInventories.put(player, new InventorySave(player));
	}

	public static void loadInventory(Player player){
	    if(!savedInventories.containsKey(player)) return;
	    savedInventories.remove(player).loadInventoryTo(player);
	}
}
