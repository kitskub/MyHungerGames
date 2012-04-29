package com.randude14.hungergames;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import static com.randude14.hungergames.Defaults.Message.*;
import static com.randude14.hungergames.Defaults.Config.*;

public class Config {// TODO defaults
	private static final Plugin plugin = Plugin.getInstance();
	
	// Global only
	public static String getDefaultGame() {
		return plugin.getConfig().getString("global.default-game");
	}
	
	public static long getUpdateDelay() {
		return plugin.getConfig().getLong("global.update-delay");
	}

	// Global
	public static String getGlobalJoinMessage() {
		return plugin.getConfig().getString("global.join-message", JOIN.getMessage());
	}
	
	public static String getGlobalRejoinMessage() {
		return plugin.getConfig().getString("global.rejoin-message", REJOIN.getMessage());
	}
	
	public static String getGlobalLeaveMessage() {
		return plugin.getConfig().getString("global.leave-message", LEAVE.getMessage());
	}
	
	public static String getGlobalKillMessage() {
		return plugin.getConfig().getString("global.kill-message", KILL.getMessage());
	}
	
	public static String getGlobalVoteMessage() {
		return plugin.getConfig().getString("global.vote-message", VOTE.getMessage());
	}
	
	public static int getGlobalMinVote() {
		return plugin.getConfig().getInt("global.min-vote", MIN_VOTE.getInt());
	}
	
	public static int getGlobalMinPlayers() {
		return plugin.getConfig().getInt("global.min-players", MIN_PLAYERS.getInt());
	}
	
	public static int getGlobalDefaultTime() {
		return plugin.getConfig().getInt("global.default-time", DEFAULT_TIME.getInt());
	}
	
	public static boolean getGlobalAllowRejoin() {
		return plugin.getConfig().getBoolean("global.allow-rejoin", ALLOW_REJOIN.getBoolean());
	}
	
	public static boolean getGlobalAllowJoinWhileRunning() {
		return plugin.getConfig().getBoolean("global.allow-join-during-game", ALLOW_JOIN_WHILE_RUNNING.getBoolean());
	}
	
	public static boolean getGlobalWinnerKeepsItems(){
		return plugin.getConfig().getBoolean("global.winner-keeps-items", WINNER_KEEPS_ITEMS.getBoolean());
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
		ConfigurationSection itemSection = config.getConfigurationSection("global.sponsor-loot");
		if(itemSection == null) return sponsorLoot;
		
		return readSponsorLoot(itemSection);
		
	}
	
	// Setups
	public static String getJoinMessage(String setup) {
		return plugin.getConfig().getString("setups." + setup + ".join-message", getGlobalJoinMessage());

	}
	
	public static String getRejoinMessage(String setup) {
		return plugin.getConfig().getString("setups." + setup + ".rejoin-message", getGlobalRejoinMessage());
	}
	
	public static String getLeaveMessage(String setup) {
		return plugin.getConfig().getString("setups." + setup + ".leave-message", getGlobalLeaveMessage());
	}
	
	public static String getKillMessage(String setup) {
		return plugin.getConfig().getString("setups." + setup + ".kill-message", getGlobalKillMessage());
	}
	
	public static String getVoteMessage(String setup) {
		return plugin.getConfig().getString("setups." + setup + ".vote-message", getGlobalVoteMessage());
	}
	
	public static int getMinVote(String setup) {
		return plugin.getConfig().getInt("setups." + setup + ".min-vote", getGlobalMinVote());
	}
	
	public static int getMinPlayers(String setup) {
		return plugin.getConfig().getInt("setups." + setup + ".min-players", getGlobalMinPlayers());
	}
	
	public static int getDefaultTime(String setup) {
		return plugin.getConfig().getInt("setups." + setup + ".default-time", getGlobalDefaultTime());
	}
	
	public static boolean getAllowRejoin(String setup) {
		return plugin.getConfig().getBoolean("setups." + setup + ".allow-rejoin", getGlobalAllowRejoin());
	}
	
	public static boolean getAllowJoinWhileRunning(String setup) {
		return plugin.getConfig().getBoolean("setups." + setup + ".allow-join-during-game", getGlobalAllowJoinWhileRunning());
	}
	
	public static boolean getWinnerKeepsItems(String setup){
		return plugin.getConfig().getBoolean("setups." + setup + ".winner-keeps-items", getGlobalWinnerKeepsItems());
	}
	
	// Itemsets
	public static Map<ItemStack, Float> getAllChestLootWithGlobal(String[] itemsets){
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    for(String s : itemsets){
		toRet.putAll(getChestLoot(s));
	    }
	    toRet.putAll(getGlobalChestLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Double> getAllSponsorLootWithGlobal(String[] itemsets){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    for(String s : itemsets){
		toRet.putAll(getSponsorLoot(s));
	    }
	    toRet.putAll(getGlobalSponsorLoot());
	    return toRet;
	}
	
	public static Map<ItemStack, Float> getChestLoot(String itemset){
	    plugin.reloadConfig();
	    FileConfiguration config = plugin.getConfig();
	    Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
	    ConfigurationSection itemSection = config.getConfigurationSection("itemsets." + itemset + ".chest-loot");
	    if(itemSection == null) return chestLoot;

	    return readChestLoot(itemSection);
	}
	
	public static Map<ItemStack, Double> getSponsorLoot(String itemset){
	    plugin.reloadConfig();
	    FileConfiguration config = plugin.getConfig();
	    Map<ItemStack, Double> chestLoot = new HashMap<ItemStack, Double>();
	    ConfigurationSection itemSection = config.getConfigurationSection("itemsets." + itemset + ".sponsor-loot");
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