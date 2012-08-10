package com.randude14.hungergames.utils;

import com.randude14.hungergames.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ConfigUtils {	
	
	public static ItemStack getItemStack(Block block) {
		return new ItemStack(block.getType(), 1, block.getData());
	}
	
	public static ItemStack getItemStack(String s, int stackSize, boolean useMatchMaterial){
		s = s.split(",")[0];
		String[] keyParts = s.split(":");
		Material mat = Material.matchMaterial(keyParts[0]);
		if(mat == null) {
			Logging.debug("Material with name {0} could not be loaded.", keyParts[0]);
			return null;
		}
		ItemStack item = new ItemStack(mat, stackSize);
		if(keyParts.length == 2){
			try{
				item.setDurability(Short.valueOf(keyParts[1]));
			}
			catch(NumberFormatException e){
				Logging.debug("Can't convert {0} to short", keyParts[1]);
			}
		}
		return item;
	}
		
	public static List<ItemStack> readItemSection(ConfigurationSection chestSection, boolean useMatchMaterial) {
		List<ItemStack> toRet = new ArrayList<ItemStack>();
		if(chestSection == null) return toRet;

		for(String key : chestSection.getKeys(false)) {
		    ConfigurationSection section = chestSection.getConfigurationSection(key);
		    ItemStack item = getItemStack(section, useMatchMaterial);
		    if(item == null) continue;
		    toRet.add(item);
		}
		return toRet;
	}
	
	public static Map<ItemStack, Float> readItemSectionWithChance(ConfigurationSection itemSection, boolean useMatchMaterial){
	    Map<ItemStack, Float> toRet = new HashMap<ItemStack, Float>();
	    if(itemSection == null) return toRet;
	    
	    for(String key : itemSection.getKeys(false)) {
		    ConfigurationSection section = itemSection.getConfigurationSection(key);
		    ItemStack item = getItemStack(section, useMatchMaterial);
		    if (item == null) continue;
		    float chance = new Double(section.getDouble("chance", 0.3333337)).floatValue();
		    toRet.put(item, chance);
	    }
	    return toRet;
	}
		
	public static Map<ItemStack, Double> readItemSectionWithMoney(ConfigurationSection itemSection, boolean useMatchMaterial){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    if(itemSection == null) return toRet;
	    
	    for(String key : itemSection.getKeys(false)) {
		    ConfigurationSection section = itemSection.getConfigurationSection(key);
		    ItemStack item = getItemStack(section, useMatchMaterial);
		    if(item == null) continue;

		    double money = section.getDouble("money", 10.00);
		    toRet.put(item, money);
	    }
	    return toRet;
	}
	
	private static ItemStack getItemStack(ConfigurationSection section, boolean useMatchMaterial) {
	    if (section == null) return null;
	    int stackSize = section.getInt("stack-size", 1);
	    ItemStack item = getItemStack(section.getName(), stackSize, useMatchMaterial);
	    if(item == null) return null;

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
	    return item;
	}
}
