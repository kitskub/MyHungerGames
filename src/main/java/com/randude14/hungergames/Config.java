package com.randude14.hungergames;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Config {// TODO defaults
	private static final Plugin plugin = Plugin.getInstance();
	
	public static String getJoinMessage() {
		return plugin.getConfig().getString("properties.join-message");
	}
	
	public static String getRejoinMessage() {
		return plugin.getConfig().getString("properties.rejoin-message");
	}
	
	public static String getLeaveMessage() {
		return plugin.getConfig().getString("properties.leave-message");
	}
	
	public static String getKillMessage() {
		return plugin.getConfig().getString("properties.kill-message");
	}
	
	public static String getVoteMessage() {
		return plugin.getConfig().getString("properties.vote-message");
	}
	
	public static String getDefaultGame() {
		return plugin.getConfig().getString("properties.default-game");
	}
	
	public static int getMinVote() {
		return plugin.getConfig().getInt("properties.min-vote");
	}
	
	public static int getMinPlayers() {
		return plugin.getConfig().getInt("properties.min-players");
	}
	
	public static int getDefaultTime() {
		return plugin.getConfig().getInt("properties.default-time");
	}
	
	public static boolean shouldAllowRejoin() {
		return plugin.getConfig().getBoolean("properties.allow-rejoin");
	}
	
	public static boolean getAllowJoinWhileRunning() {
		return plugin.getConfig().getBoolean("properties.allow-join-during-game");
	}
	
	public static long getUpdateDelay() {
		return plugin.getConfig().getLong("properties.update-delay");
	}
	
	public static boolean getWinnerKeepsItems(){
		return plugin.getConfig().getBoolean("properties.winner-keeps-items");
	}
	
	public static boolean shouldRespawnAtSpawnPoint() {
		return plugin.getConfig().getBoolean("spawnpoint-on-death");
	}
	
	public static Map<ItemStack, Float> getGlobalChestLoot() {// TODO multiple different game
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
		ConfigurationSection itemSection = config.getConfigurationSection("global.chest-loot");
		if(itemSection == null) return chestLoot;
		
		return readChestLoot(itemSection);
	}
	
	public static Map<ItemStack, Double> getGlobalSponsorLoot() {
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		Map<ItemStack, Double> sponsorLoot = new HashMap<ItemStack, Double>();
		ConfigurationSection itemSection = config.getConfigurationSection("sponsor-loot");
		if(itemSection == null) return sponsorLoot;
		
		return readSponsorLoot(itemSection);
		
	}
	
	public static Map<ItemStack, Float> getAllChestLootWithGlobal(String[] setups){
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    for(String s : setups){
		toRet.putAll(getChestLoot(s));
	    }
	    toRet.putAll(getGlobalChestLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Double> getAllSponsorLootWithGlobal(String[] setups){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    for(String s : setups){
		toRet.putAll(getSponsorLoot(s));
	    }
	    toRet.putAll(getGlobalSponsorLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Float> getChestLoot(String setup){
	    plugin.reloadConfig();
	    FileConfiguration config = plugin.getConfig();
	    Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
	    ConfigurationSection itemSection = config.getConfigurationSection("setups." + setup + ".chest-loot");
	    if(itemSection == null) return chestLoot;

	    return readChestLoot(itemSection);
	}
	
	public static Map<ItemStack, Double> getSponsorLoot(String setup){
	    plugin.reloadConfig();
	    FileConfiguration config = plugin.getConfig();
	    Map<ItemStack, Double> chestLoot = new HashMap<ItemStack, Double>();
	    ConfigurationSection itemSection = config.getConfigurationSection("setups." + setup + ".sponsor-loot");
	    if(itemSection == null) return chestLoot;

	    return readSponsorLoot(itemSection);
	}
	
	public static void addChestLoot(ItemStack item, float f){
	    // TODO chest loot by command
	}
	
	public static void addSponsorLoot(ItemStack item, double f){
	    // TODO sponsor loot by command
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
	
	private static ItemStack getItemStack(String s, int stackSize){
	    String[] keyParts = s.split(":");
	    Material mat = Material.matchMaterial(keyParts[0]);
	    if(mat == null) return null;
	    MaterialData data = new MaterialData(mat);
	    if(keyParts.length == 2){
		try{
		    data.setData(Integer.valueOf(keyParts[1]).byteValue());
		}
		catch(NumberFormatException e){}
	    }
	    ItemStack item = new ItemStack(mat, stackSize);
	    item.setData(data);
	    
	    return item;
	}
	
}