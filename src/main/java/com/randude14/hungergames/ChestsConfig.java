package com.randude14.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ChestsConfig {
	private static final HungerGames plugin = HungerGames.getInstance();
	private static CustomYaml chestConfig = new CustomYaml(new File(HungerGames.getInstance().getDataFolder(), "chestconfig.yml"));
	
	public static void reload() {
		chestConfig.load();
	}
	
	public static boolean useMatchMaterial() {
		return chestConfig.getConfig().getBoolean("global.use-match-material", Defaults.Config.USE_MATCH_MATERIAL.getBoolean());
	}
	
	// Itemsets
	public static List<String> getItemSets(){
	    ConfigurationSection section = chestConfig.getConfig().getConfigurationSection("itemsets");
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
		chestLoot.putAll(readChestLoot(chestConfig.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot")));
		checked.add(itemset);
		for (String parent : chestConfig.getConfig().getStringList("itemsets." + itemset + ".inherits")) {
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
		chestLoot.putAll(readSponsorLoot(chestConfig.getConfig().getConfigurationSection("itemsets." + itemset + ".sponsor-loot")));
		checked.add(itemset);
		for (String parent : chestConfig.getConfig().getStringList("itemsets." + itemset + ".inherits")) {
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
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    ConfigurationSection itemSection = null;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = chestConfig.getConfig().getConfigurationSection("global.chest-loot");
	    }
	    else {
		    itemSection = chestConfig.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot");
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
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    ConfigurationSection itemSection = null;
	    if (itemset == null || itemset.equalsIgnoreCase("")){
		    itemSection = chestConfig.getConfig().getConfigurationSection("global.chest-loot");
	    }
	    else {
		    itemSection = chestConfig.getConfig().getConfigurationSection("itemsets." + itemset + ".chest-loot");
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
	
	private static Map<ItemStack, Float> readChestLoot(ConfigurationSection itemSection){
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    if(itemSection == null) return toRet;
	    
	    for(String key : itemSection.getKeys(false)) {
		    ConfigurationSection section = itemSection.getConfigurationSection(key);
		    int stackSize = section.getInt("stack-size", 1);
		    ItemStack item = getItemStack(key, stackSize);
		    if(item == null) continue;
		    
		    for(String str : section.getKeys(false)) {
			    Enchantment enchant = Enchantment.getByName(str);
			    if(enchant == null || !enchant.canEnchantItem(item)) {
				    continue;
			    }
			    int level = section.getInt(str, 1);
			    try {
				    item.addEnchantment(enchant, level);
			    } catch (Exception ex) {
			    }
		    }
		    
		    float chance = new Double(section.getDouble("chance", 0.3333337)).floatValue();
		    toRet.put(item, chance);
	    }
	    return toRet;
	}
	
	private static Map<ItemStack, Double> readSponsorLoot(ConfigurationSection itemSection){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    if(itemSection == null) return toRet;
	    
	    for(String key : itemSection.getKeys(false)) {
		    ConfigurationSection section = itemSection.getConfigurationSection(key);
		    int stackSize = section.getInt("stack-size", 1);
		    ItemStack item = getItemStack(key, stackSize);
		    if(item == null) continue;

		    for(String str : section.getKeys(false)) {
			    Enchantment enchant = Enchantment.getByName(str);
			    if(enchant == null || !enchant.canEnchantItem(item)) {
				    continue;
			    }
			    int level = section.getInt(str, 1);
			    try {
				    item.addEnchantment(enchant, level);
			    } catch (Exception ex) {
			    }

		    }

		    double money = section.getDouble("money", 10.00);
		    toRet.put(item, money);
	    }
	    return toRet;
	}
	
	public static Map<ItemStack, Float> getGlobalChestLoot() {
		plugin.reloadConfig();
		FileConfiguration config = chestConfig.getConfig();
		Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
		ConfigurationSection itemSection = config.getConfigurationSection("global.chest-loot");
		if(itemSection == null) return chestLoot;
		
		return readChestLoot(itemSection);
	}
	
	public static Map<ItemStack, Double> getGlobalSponsorLoot() {
		plugin.reloadConfig();
		FileConfiguration config = chestConfig.getConfig();
		Map<ItemStack, Double> sponsorLoot = new HashMap<ItemStack, Double>();
		ConfigurationSection itemSection = config.getConfigurationSection("global.sponsor-loot");
		if(itemSection == null) return sponsorLoot;
		
		return readSponsorLoot(itemSection);
	}


	public static Set<String> getFixedChests() {
		return chestConfig.getConfig().getConfigurationSection("chests").getKeys(false);
	}
	
	private static List<ItemStack> getFixedChest(String chest, Set<String> checked) {
		List<ItemStack> fixedChests = new ArrayList<ItemStack>();
		if (checked.contains(chest)) return fixedChests;
		fixedChests.addAll(readFixedChest(chestConfig.getConfig().getConfigurationSection("chests." + chest)));
		checked.add(chest);
		for (String parent : chestConfig.getConfig().getStringList("chests." + chest + ".inherits")) {
			fixedChests.addAll(getFixedChest(parent, checked));
		}
		return fixedChests;
	}
	
	public static List<ItemStack> getFixedChest(String section) {
		return getFixedChest(section, new HashSet<String>());
	}
			
	public static List<ItemStack> readFixedChest(ConfigurationSection chestSection) {
		List<ItemStack> toRet = new ArrayList<ItemStack>();
		if(chestSection == null) return toRet;

		for(String key : chestSection.getKeys(false)) {
		    ConfigurationSection section = chestSection.getConfigurationSection(key);
		    int stackSize = section.getInt("stack-size", 1);
		    ItemStack item = getItemStack(key, stackSize);
		    if(item == null) continue;

		    for(String str : section.getKeys(false)) {
			    Enchantment enchant = Enchantment.getByName(str);
			    if(enchant == null || !enchant.canEnchantItem(item)) {
				    continue;
			    }
			    int level = section.getInt(str, 1);
			    try {
				    item.addEnchantment(enchant, level);
			    } catch (Exception ex) {
			    }
		    }
		    toRet.add(item);
		}
		return toRet;
	}
	
	private static ItemStack getItemStack(String s, int stackSize){
		String[] keyParts = s.split(",")[0].split(":");
		int id = -1;
		if (useMatchMaterial()) {
			id = Material.matchMaterial(keyParts[0]).getId();
		}
		else {
			try {
				id = Integer.parseInt(keyParts[0]);
			} catch (NumberFormatException numberFormatException) {
				id = -1;
			}
		}
		if(id == -1) return null;
		MaterialData data = new MaterialData(id);
		if(keyParts.length == 2){
			try{
				data.setData(Integer.valueOf(keyParts[1]).byteValue());
			}
			catch(NumberFormatException e){}
		}
		ItemStack item = new ItemStack(id, stackSize);
		item.setData(data);
	    
		return item;
	}
}
