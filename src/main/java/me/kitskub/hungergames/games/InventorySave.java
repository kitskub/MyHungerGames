package me.kitskub.hungergames.games;

import com.google.common.base.Stopwatch;
import java.util.HashMap;
import java.util.Map;
import me.kitskub.hungergames.HungerGames;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventorySave {
	private static final Map<Player, InventorySave> savedInventories = new HashMap<Player, InventorySave>();
	private static final Map<Player, InventorySave> savedGameInventories = new HashMap<Player, InventorySave>();
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;
	private float exp = 0;
	private int level = 0;
	private boolean noClear;

	private InventorySave setNoClear(boolean clear) {
		this.noClear = clear;
		return this;
	}

	private InventorySave(Player player) {
		armorContents = player.getInventory().getArmorContents();
		contents = player.getInventory().getContents();
		exp = player.getExp();
		level = player.getLevel();
	}

	private void loadInventoryTo(Player player) {
		// Counter intuitive - if the inventory wasn't cleared before, we only want to give them what they had before.
		if (noClear) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		}
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armorContents);
		if (!noClear) {
			player.setLevel(level);
			player.setExp(exp);
		}
		player.updateInventory();
	}
	
	public static void saveAndClearInventory(Player player){
		savedInventories.put(player, new InventorySave(player));
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		player.getInventory().clear();
		player.setLevel(0);
		player.setExp(0);
		player.updateInventory();
	}
	
	public static void saveInventoryNoClear(Player player){
		savedInventories.put(player, new InventorySave(player).setNoClear(true));
		player.updateInventory();
	}

	public static void loadInventory(Player player){
		if(!savedInventories.containsKey(player)) return;
		savedInventories.remove(player).loadInventoryTo(player);
	}
	
	public static void saveAndClearGameInventory(Player player){
		savedGameInventories.put(player, new InventorySave(player));
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		player.getInventory().clear();
		player.setLevel(0);
		player.setExp(0);
	}

	public static void loadGameInventory(Player player){
		if(!savedGameInventories.containsKey(player)) return;
		HungerGames.TimerManager.start();
		savedGameInventories.remove(player).loadInventoryTo(player);
		HungerGames.TimerManager.stop("InventorySave.loadGameInventory");
	}
}
