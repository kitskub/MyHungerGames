package com.randude14.hungergames;

import static com.randude14.hungergames.utils.ConfigUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemConfig {
	
	public static boolean useMatchMaterial() {
		return Files.ITEMCONFIG.getConfig().getBoolean("global.use-match-material", Defaults.Config.USE_MATCH_MATERIAL.getBoolean());
	}
	
	// Itemsets
	public static List<String> getItemSets(){
	    ConfigurationSection section = Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets");
	    if(section == null) return Collections.emptyList();
	    List<String> list = new ArrayList<String>(section.getKeys(false));
	    return (list == null) ? new ArrayList<String>() : list;
	}
	
	public static Map<ItemStack, Float> getAllChestLootWithGlobal(List<String> itemsets){
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
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
	
	/** For safe recursiveness */
	private static Map<ItemStack, Float> getChestLoot(String itemset, Set<String> checked) {
		Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
		if (checked.contains(itemset)) return chestLoot;
		chestLoot.putAll(readItemSectionWithChance(Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot"), useMatchMaterial()));
		checked.add(itemset);
		for (String parent : Files.ITEMCONFIG.getConfig().getStringList("itemsets." + itemset + ".inherits")) {
			chestLoot.putAll(getChestLoot(parent, checked));
		}
		return chestLoot;
	}
	
	public static Map<ItemStack, Float> getChestLoot(String itemset){
		return getChestLoot(itemset, new HashSet<String>());
	}
	
	/** For safe recursiveness */
	private static Map<ItemStack, Double> getSponsorLoot(String itemset, Set<String> checked) {
		Map<ItemStack, Double> chestLoot = new HashMap<ItemStack, Double>();
		if (checked.contains(itemset)) return chestLoot;
		chestLoot.putAll(readItemSectionWithMoney(Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets." + itemset + ".sponsor-loot"), useMatchMaterial()));
		checked.add(itemset);
		for (String parent : Files.ITEMCONFIG.getConfig().getStringList("itemsets." + itemset + ".inherits")) {
			checked.add(parent);
			chestLoot.putAll(getSponsorLoot(parent, checked));
		}
		return chestLoot;
	}
	public static Map<ItemStack, Double> getSponsorLoot(String itemset){
		return getSponsorLoot(itemset, new HashSet<String>());
	}
	
	
	/**
	 * Adds itemstack to chestLoot of the itemset provided, or global if itemset is empty or null
	 * @param itemset
	 * @param item
	 * @param chance
	 */
	public static void addChestLoot(String itemset, ItemStack item, float chance){
	    ConfigurationSection itemSection = null;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("global.chest-loot");
		    if (itemSection == null) {
			    itemSection = Files.ITEMCONFIG.getConfig().createSection("global.chest-loot");
		    }
	    }
	    else {
		    itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot");
		    if (itemSection == null) {
			    itemSection = Files.ITEMCONFIG.getConfig().createSection("itemsets." + itemset + ".chest-loot");
		    }
	    }
	    StringBuilder builder = new StringBuilder();
	    builder.append(item.getTypeId());
	    builder.append(item.getData().getData());
	    builder.append(",");
	    builder.append(System.currentTimeMillis());
	    itemSection = itemSection.createSection(builder.toString());
	    itemSection.set("stack-size", item.getAmount());
	    itemSection.set("chance", chance);
	    for (Enchantment enchantment : item.getEnchantments().keySet()) {
		    itemSection.set(enchantment.getName(), item.getEnchantments().get(enchantment));
	    }
	}
	
	public static void addSponsorLoot(String itemset, ItemStack item, double cost){
	    ConfigurationSection itemSection = null;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("global.chest-loot");
	    }
	    else {
		    itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot");
	    }
	    StringBuilder builder = new StringBuilder();
	    builder.append(item.getTypeId());
	    builder.append(item.getData().getData());
	    itemSection = itemSection.createSection(builder.toString());
	    itemSection.set("stack-size", item.getAmount());
	    itemSection.set("money", cost);
	    for (Enchantment enchantment : item.getEnchantments().keySet()) {
		    itemSection.set(enchantment.getName(), item.getEnchantments().get(enchantment));
	    }
	}
	
	public static Map<ItemStack, Float> getGlobalChestLoot() {
		Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
		ConfigurationSection itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("global.chest-loot");
		if(itemSection == null) return chestLoot;
		
		return readItemSectionWithChance(itemSection, useMatchMaterial());
	}
	
	public static Map<ItemStack, Double> getGlobalSponsorLoot() {
		Map<ItemStack, Double> sponsorLoot = new HashMap<ItemStack, Double>();
		ConfigurationSection itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("global.sponsor-loot");
		if(itemSection == null) return sponsorLoot;
		
		return readItemSectionWithMoney(itemSection, useMatchMaterial());
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
		return readItemSection(Files.ITEMCONFIG.getConfig().getConfigurationSection("kits." + kit), useMatchMaterial());
	}
	
	private static List<ItemStack> getFixedChest(String chest, Set<String> checked) {
		List<ItemStack> fixedChests = new ArrayList<ItemStack>();
		if (checked.contains(chest)) return fixedChests;
		fixedChests.addAll(readItemSection(Files.ITEMCONFIG.getConfig().getConfigurationSection("chests." + chest), useMatchMaterial()));
		checked.add(chest);
		for (String parent : Files.ITEMCONFIG.getConfig().getStringList("chests." + chest + ".inherits")) {
			fixedChests.addAll(getFixedChest(parent, checked));
		}
		return fixedChests;
	}
	
	public static List<ItemStack> getFixedChest(String section) {
		return getFixedChest(section, new HashSet<String>());
	}
	
	public static List<ItemStack> getStaticRewards() {
		return readItemSection(Files.ITEMCONFIG.getConfig().getConfigurationSection("rewards.static"), useMatchMaterial());
			
	}
	
	public static Map<ItemStack, Float> getRandomRewards() {
		return readItemSectionWithChance(Files.ITEMCONFIG.getConfig().getConfigurationSection("rewards.random"), useMatchMaterial());
			
	}
	
	public static void addStaticReward(ItemStack item) {
		ConfigurationSection itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("rewards.static");
		StringBuilder builder = new StringBuilder();
		builder.append(item.getTypeId());
		builder.append(item.getData().getData());
		builder.append(",");
		builder.append(System.currentTimeMillis());
		itemSection = itemSection.createSection(builder.toString());
		itemSection.set("stack-size", item.getAmount());
		for (Enchantment enchantment : item.getEnchantments().keySet()) {
		    itemSection.set(enchantment.getName(), item.getEnchantments().get(enchantment));
		}
	}
	
	public static void addRandomReward(ItemStack item, float chance) {
		ConfigurationSection itemSection = Files.ITEMCONFIG.getConfig().getConfigurationSection("rewards.random");
		StringBuilder builder = new StringBuilder();
		builder.append(item.getTypeId());
		builder.append(item.getData().getData());
		builder.append(",");
		builder.append(System.currentTimeMillis());
		itemSection = itemSection.createSection(builder.toString());
		itemSection.set("stack-size", item.getAmount());
		itemSection.set("chance", chance);
		for (Enchantment enchantment : item.getEnchantments().keySet()) {
		    itemSection.set(enchantment.getName(), item.getEnchantments().get(enchantment));
		}
	}
	
	public static int getMaxRandomItems() {
		return Files.ITEMCONFIG.getConfig().getInt("rewards.max-random", Defaults.Config.MAX_RANDOM_ITEMS.getInt());
	}
}
