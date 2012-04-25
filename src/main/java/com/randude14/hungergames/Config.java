package com.randude14.hungergames;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Config {
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
	
	public static int getDefaultTime() {
		return plugin.getConfig().getInt("properties.default-time");
	}
	
	public static Map<ItemStack, Float> getChestLoot() {
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		Map<ItemStack, Float> chestLoot = new HashMap<ItemStack, Float>();
		ConfigurationSection itemSection = config.getConfigurationSection("chest-loot");
		if(itemSection == null) {
			return chestLoot;
		}
		for(String key : itemSection.getKeys(false)) {
			ConfigurationSection section = itemSection.getConfigurationSection(key);
			Material mat = Material.getMaterial(key);
			if(mat == null)
				continue;
			int stackSize = section.getInt("stack-size", 1);
			float chance = new Double(section.getDouble("chance", 0.3333337)).floatValue();
			ItemStack item;
			if(section.isInt("data")) {
				byte data = new Integer(section.getInt("data")).byteValue();
				item = new ItemStack(mat, stackSize, data);
			}
			
			else {
				item = new ItemStack(mat, stackSize);
			}
			
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
			chestLoot.put(item, chance);
		}
		return chestLoot;
	}
	
	public static Map<ItemStack, Double> getSponsorLoot() {
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		Map<ItemStack, Double> sponsorLoot = new HashMap<ItemStack, Double>();
		ConfigurationSection itemSection = config.getConfigurationSection("sponsor-loot");
		if(itemSection == null) {
			return sponsorLoot;
		}
		for(String key : itemSection.getKeys(false)) {
			ConfigurationSection section = itemSection.getConfigurationSection(key);
			Material mat = Material.getMaterial(key);
			if(mat == null)
				continue;
			int stackSize = section.getInt("stack-size", 1);
			double money = section.getDouble("money", 10.00);
			ItemStack item;
			if(section.isInt("data")) {
				byte data = new Integer(section.getInt("data")).byteValue();
				item = new ItemStack(mat, stackSize, data);
			}
			
			else {
				item = new ItemStack(mat, stackSize);
			}
			
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
			sponsorLoot.put(item, money);
		}
		return sponsorLoot;
		
	}
	
}
