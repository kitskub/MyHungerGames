package com.randude14.hungergames;

import static com.randude14.hungergames.utils.ConfigUtils.*;
import com.randude14.hungergames.utils.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemConfig {
	
	public static boolean useMatchMaterial() {
		return Files.ITEMCONFIG.getConfig().getBoolean("global.use-match-material", Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean());
	}
	
	// Itemsets
	public static List<String> getItemSets(){
	    ConfigurationSection section = Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets");
	    if(section == null) return Collections.emptyList();
	    List<String> list = new ArrayList<String>(section.getKeys(false));
	    return (list == null) ? new ArrayList<String>() : list;
	}
	
	public static Map<ItemStack, Double> getAllChestLootWithGlobal(List<String> itemsets){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    if(itemsets != null) {
		for(String s : itemsets){
			toRet.putAll(getChestLoot(s));
		}
	    }
	    toRet.putAll(getGlobalChestLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Double> getAllSponsorLootWithGlobal(List<String> itemsets){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    if(itemsets != null){
		for(String s : itemsets){
			toRet.putAll(getSponsorLoot(s));
		}
	    }
	    toRet.putAll(getGlobalSponsorLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Double> getLoot(String itemset, Set<String> checked, String type, String value, double def) {
		Map<ItemStack, Double> chestLoot = new HashMap<ItemStack, Double>();
		if (checked.contains(itemset)) return chestLoot;
		for (Item i : getItemSection(Files.ITEMCONFIG.getConfig(), "itemsets." + itemset + "." + type, useMatchMaterial())) {
			Object get = i.getValues().get(value);
			chestLoot.put(i.getStack(), (get instanceof Double) ? (Double) get : def);
		}
		checked.add(itemset);
		for (String parent : Files.ITEMCONFIG.getConfig().getStringList("itemsets." + itemset + ".inherits")) {
			checked.add(parent);
			chestLoot.putAll(getLoot(parent, checked, type, value, def));
		}
		return chestLoot;
	}

	public static Map<ItemStack, Double> getChestLoot(String itemset){
		return getLoot(itemset, new HashSet<String>(), "chest-loot", CHANCE, .333);
	}

	public static Map<ItemStack, Double> getSponsorLoot(String itemset){
		return getLoot(itemset, new HashSet<String>(), "sponsor-loot", MONEY, 10.00);
	}
	
	
	/**
	 * Adds itemstack to chestLoot of the itemset provided, or global if itemset is empty or null
	 * @param itemset
	 * @param item
	 * @param chance
	 */
	public static void addChestLoot(String itemset, ItemStack item, double chance){
	    String itemSection;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = "global.chest-loot";
	    }
	    else {
		    itemSection = "itemsets." + itemset + ".chest-loot";
	    }
	    List<Item> list = getItemSection(Files.ITEMCONFIG.getConfig(), itemSection, useMatchMaterial());
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put(CHANCE, chance);
	    list.add(new Item(item, map));
	    Files.ITEMCONFIG.getConfig().set(itemSection, list);
	}
	
	public static void addSponsorLoot(String itemset, ItemStack item, double cost){
	    String itemSection;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = "global.sponsor-loot";
	    }
	    else {
		    itemSection = "itemsets." + itemset + ".sponsor-loot";
	    }
	    List<Item> list = getItemSection(Files.ITEMCONFIG.getConfig(), itemSection, useMatchMaterial());
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put(MONEY, cost);
	    list.add(new Item(item, map));
	    Files.ITEMCONFIG.getConfig().set(itemSection, list);
	}
	
	public static Map<ItemStack, Double> getGlobalChestLoot() {
		Map<ItemStack, Double> chestLoot = new HashMap<ItemStack, Double>();
		for (Item i : getItemSection(Files.ITEMCONFIG.getConfig(), "global.chest-loot", useMatchMaterial())) {
			Object get = i.getValues().get(CHANCE);
			chestLoot.put(i.getStack(), (get instanceof Double) ? (Double) get : .333);
		}
		return chestLoot;
	}
	
	public static Map<ItemStack, Double> getGlobalSponsorLoot() {
		Map<ItemStack, Double> sponsorLoot = new HashMap<ItemStack, Double>();
		for (Item i : getItemSection(Files.ITEMCONFIG.getConfig(), "global.sponsor-loot", useMatchMaterial())) {
			Object get = i.getValues().get(CHANCE);
			sponsorLoot.put(i.getStack(), (get instanceof Double) ? (Double) get : .333);
		}
		return sponsorLoot;
	}

	public static Set<String> getFixedChests() {
		ConfigurationSection chestSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("chests");
		if (chestSection == null) return new HashSet<String>();
		return chestSection.getKeys(false);
	}

	public static Set<String> getKits() {
		ConfigurationSection chestSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("kits");
		if (chestSection == null) return new HashSet<String>();
		return chestSection.getKeys(false);
	}
	
	public static List<ItemStack> getKit(String kit) {
		return toItemStackList(getItemSection(Files.ITEMCONFIG.getConfig(), "kits." + kit, useMatchMaterial()));
	}
	
	private static List<ItemStack> getFixedChest(String chest, Set<String> checked) {
		List<ItemStack> fixedChests = new ArrayList<ItemStack>();
		if (checked.contains(chest)) return fixedChests;
		fixedChests.addAll(toItemStackList(getItemSection(Files.ITEMCONFIG.getConfig(), "chests." + chest, useMatchMaterial())));
		checked.add(chest);
		for (String parent : Files.ITEMCONFIG.getConfig().getStringList("chests." + chest + ".inherits")) {
			fixedChests.addAll(getFixedChest(parent, checked));
		}
		return fixedChests;
	}
	
	private static List<ItemStack> toItemStackList(List<Item> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (Item i : items) {
			list.add(i.getStack());
		}
		return list;
	}

	public static List<ItemStack> getFixedChest(String section) {
		return getFixedChest(section, new HashSet<String>());
	}
	
	public static List<ItemStack> getStaticRewards() {
		return toItemStackList(getItemSection(Files.ITEMCONFIG.getConfig(), "rewards.static", useMatchMaterial()));
			
	}
	
	public static Map<ItemStack, Double> getRandomRewards() {
		Map<ItemStack, Double> rewards = new HashMap<ItemStack, Double>();
		for (Item i : getItemSection(Files.ITEMCONFIG.getConfig(), "rewards.random", useMatchMaterial())) {
			Object get = i.getValues().get(CHANCE);
			rewards.put(i.getStack(), (get instanceof Double) ? (Double) get : .333);
		}
		return rewards;			
	}
	
	public static void addStaticReward(ItemStack item) {
		List<Item> list = getItemSection(Files.ITEMCONFIG.getConfig(), "rewards.static", useMatchMaterial());
		list.add(new Item(item, null));
		Files.ITEMCONFIG.getConfig().set("rewards.static", list);
	}
	
	public static void addRandomReward(ItemStack item, double chance) {
		List<Item> list = getItemSection(Files.ITEMCONFIG.getConfig(), "rewards.random", useMatchMaterial());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CHANCE, chance);
		list.add(new Item(item, map));
		Files.ITEMCONFIG.getConfig().set("rewards.random", list);
	}
	
	public static int getMaxRandomItems() {
		return Files.ITEMCONFIG.getConfig().getInt("rewards.max-random", Defaults.ItemConfig.MAX_RANDOM_ITEMS.getInt());
	}
}
