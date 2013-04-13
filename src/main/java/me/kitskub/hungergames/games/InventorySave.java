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

	private InventorySave(Player player) {
		armorContents = player.getInventory().getArmorContents();
		contents = player.getInventory().getContents();
		exp = player.getExp();
		level = player.getLevel();
	}

	private void loadInventoryTo(Player player) {
		player.getInventory().setContents(contents);
		player.getInventory().setArmorContents(armorContents);
		player.setLevel(level);
		player.setExp(exp);
	}
	
	public static void saveAndClearInventory(Player player){
		savedInventories.put(player, new InventorySave(player));
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
		player.getInventory().clear();
		player.setLevel(0);
		player.setExp(0);
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
