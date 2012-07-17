package com.randude14.hungergames.games;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventorySave {
	private static final Map<Player, InventorySave> savedInventories = new HashMap<Player, InventorySave>();
	private static final Map<Player, InventorySave> savedGameInventories = new HashMap<Player, InventorySave>();
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;

	private InventorySave(Player player) {
		armorContents = player.getInventory().getArmorContents();
		contents = player.getInventory().getContents();
	}

	private void loadInventoryTo(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armorContents);
	}
	
	public static void saveAndClearInventory(Player player){
	    savedInventories.put(player, new InventorySave(player));
	    player.getInventory().clear();
	}

	public static void loadInventory(Player player){
	    if(!savedInventories.containsKey(player)) return;
	    savedInventories.remove(player).loadInventoryTo(player);
	}
	
	public static void saveAndClearGameInventory(Player player){
		savedGameInventories.put(player, new InventorySave(player));
		player.getInventory().clear();
	}

	public static void loadGameInventory(Player player){
	    if(!savedGameInventories.containsKey(player)) return;
	    savedGameInventories.remove(player).loadInventoryTo(player);
	}
}
