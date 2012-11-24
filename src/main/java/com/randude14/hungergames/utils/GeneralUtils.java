package com.randude14.hungergames.utils;

import com.google.common.base.Strings;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.ItemConfig;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.WorldNotFoundException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GeneralUtils {
	public static boolean equals(Location loc1, Location loc2) {
		return loc1.getWorld() == loc2.getWorld()
			&& loc1.getBlockX() == loc2.getBlockX()
			&& loc1.getBlockY() == loc2.getBlockY()
			&& loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	public static String parseToString(Location loc) {
		if (loc == null) return "";
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		df.setGroupingUsed(false);
		return String.format("%s %s %s %s %s %s", df.format((Number) loc.getX()), df.format((Number) loc.getY()), df.format((Number) loc.getZ()), df.format((Number) loc.getYaw()), 
			df.format((Number) loc.getPitch()), loc.getWorld().getName());
	}

	public static Location parseToLoc(String str) throws NumberFormatException, WorldNotFoundException, IllegalArgumentException {
		Strings.emptyToNull(str);
		if (str == null) {
			throw new IllegalArgumentException("Location can not be null.");
		}
		String[] strs = str.split(" ");
		double x = Double.parseDouble(strs[0]);
		double y = Double.parseDouble(strs[1]);
		double z = Double.parseDouble(strs[2]);
		float yaw = Float.parseFloat(strs[3]);
		float pitch = Float.parseFloat(strs[4]);
		World world = Bukkit.getServer().getWorld(strs[5]);
		if (world == null) throw new WorldNotFoundException("Could not load world \"" + strs[5] + "\" when loading location \"" + str);
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static String formatTime(int time) {

		List<String> strs = new ArrayList<String>();
		if(time > 3600) {
			strs.add(String.format("%d hour(s)", (time / 3600) % 24));
		}
		if(time > 60) {
			strs.add(String.format("%d minute(s)", (time / 60) % 60));
		}
		strs.add(String.format("%d second(s)", time % 60));
		StringBuilder buff = new StringBuilder();
		String sep = "";
		for (String str : strs) {
			buff.append(sep);
			buff.append(str);
			sep = ", ";
		}
		return buff.toString();
	}
	
	public static boolean hasInventoryBeenCleared(Player player) {
		PlayerInventory inventory = player.getInventory();
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		return true;
	}

	public static void fillFixedChest(Chest chest, String name) {
		chest.getInventory().clear();
		List<ItemStack> items = ItemConfig.getFixedChest(name);
		for (ItemStack stack : items) {
			int index = 0;
			do {
				index = HungerGames.getRandom().nextInt(chest.getInventory().getSize());
			} while (chest.getInventory().getItem(index) != null);
			
			chest.getInventory().setItem(index, stack);
		}
	}
	
	public static void fillChest(Chest chest, float weight, List<String> itemsets) {
		if (ItemConfig.getGlobalChestLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
			return;
		}

		chest.getInventory().clear();
		Map<ItemStack, Float> itemMap = ItemConfig.getAllChestLootWithGlobal(itemsets);
		List<ItemStack> items = new ArrayList<ItemStack>(itemMap.keySet());
		int size = chest.getInventory().getSize();
		final int maxItemSize = 100;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		int minItems = (int) Math.floor(numItems/2);
		int itemsIn = 0;
		for (int cntr = 0; cntr < numItems || itemsIn < minItems; cntr++) {
			int index = 0;
			do {
				index = HungerGames.getRandom().nextInt(chest.getInventory().getSize());
			} while (chest.getInventory().getItem(index) != null);
			
			ItemStack item = items.get(HungerGames.getRandom().nextInt(items.size()));
			if (weight * itemMap.get(item) >= HungerGames.getRandom().nextFloat()) {
				chest.getInventory().setItem(index, item);
				itemsIn++;
			}

		}
	}

	public static void rewardPlayer(Player player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.addAll(ItemConfig.getStaticRewards());
		Logging.debug("rewardPlayer: items after static: " + ArrayUtils.toString(items));
		Map<ItemStack, Float> itemMap = ItemConfig.getRandomRewards();

		int size = ItemConfig.getMaxRandomItems();
		final int maxItemSize = 25;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		Logging.debug("rewardPlayer: items after random: " + ArrayUtils.toString(items));
		for (int cntr = 0; cntr < numItems; cntr++) {			
			ItemStack item = null;
			while (item == null) { // TODO items should not have any null elements, but do.
				item = items.get(HungerGames.getRandom().nextInt(items.size()));
			}
			if (itemMap.get(item) >= HungerGames.getRandom().nextFloat()) {
				items.add(item);
			}

		}
		for (ItemStack i : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
			player.getLocation().getWorld().dropItem(player.getLocation(), i);
		}
	}
}
