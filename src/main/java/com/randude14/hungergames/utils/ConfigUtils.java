package com.randude14.hungergames.utils;

import com.randude14.hungergames.Files;
import com.randude14.hungergames.Logging;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ConfigUtils {
	public static final String MONEY = "money";
	public static final String CHANCE = "chance";
	
	public static ConfigurationSection getOrCreateSection(ConfigurationSection section, String string) {
		ConfigurationSection config = section.getConfigurationSection(string);
		if (config == null) {
			config = section.createSection(string);
		}
		return config;
	}
	
	public static ItemStack getItemStack(Block block) {
		return new ItemStack(block.getType(), 1, block.getData());
	}
	
	public static class MatData {
		public MaterialData data;
		public boolean explicit;

		public MatData(MaterialData data, boolean explicit) {
			this.data = data;
			this.explicit = explicit;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			if (explicit) {
				hash ^= data.hashCode();
			} else {
				hash ^= data.getItemTypeId();
			}
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MatData other = (MatData) obj;
			if (!other.explicit || !explicit) { // Only check id
				return data.getItemTypeId() == other.data.getItemTypeId();
			} else {
				return data.equals(other.data);
			}
		}
		
		
	}
	public static MatData getMatData(String s, boolean useMatchMaterial) {
		s = s.split(",")[0];
		String[] keyParts = s.split(":");
		Material mat = Material.matchMaterial(keyParts[0]);
		if(mat == null) {
			Logging.debug("Material with name {0} could not be loaded.", keyParts[0]);
			return null;
		}
		if(keyParts.length == 2){
			try{
				return new MatData(new MaterialData(mat, Byte.valueOf(keyParts[1])), true);
			}
			catch(NumberFormatException e){
				Logging.debug("Can't convert {0} to byte. Ignoring data.", keyParts[1]);
			}
		}
		return new MatData(new MaterialData(mat), false);
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
	
	public static List<Item> getItemSection(ConfigurationSection section, String name, boolean useMatchMaterial) {
		List<Item> toRet = new ArrayList<Item>();
		if(section == null) return toRet;
		
		List<?> list = section.getList(name);
		if (list == null || list.isEmpty()) return toRet;
		
		if (list.get(0) instanceof ConfigurationSection) return convertSection(section, name, useMatchMaterial);
		for (Object o : list) {
			toRet.add((Item) o);
		}
		return toRet;

	}
	
	public static List<Item> convertSection(ConfigurationSection section, String name, boolean useMatchMaterial) {
		List<Item> toRet = new ArrayList<Item>();
		ConfigurationSection chestSection = section.getConfigurationSection(name);
		if(chestSection == null) return toRet;

		for(String key : chestSection.getKeys(false)) {
		    ConfigurationSection keySection = chestSection.getConfigurationSection(key);
		    ItemStack stack = getItemStack(keySection, useMatchMaterial);
		    if(stack == null) continue;
		    Item i = new Item(stack, null);
		    if (keySection.contains("money")) {
			    i.getValues().put("money", keySection.get("money"));
		    }
		    if (keySection.contains("chance")) {
			    i.getValues().put("chance", keySection.get("chance"));
		    }
		    toRet.add(i);
		}
		section.set(name, toRet);
		return toRet;
		
	}

	/*
	public static List<ItemStack> readItemStackSection(ConfigurationSection chestSection, boolean useMatchMaterial) {
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

	public static Map<ItemStack, Double> readItemStackSectionWithValue(ConfigurationSection itemSection, boolean useMatchMaterial, String value, double def){
	    Map<ItemStack, Double> toRet = new HashMap<ItemStack, Double>();
	    if(itemSection == null) return toRet;
	    
	    for(String key : itemSection.getKeys(false)) {
		    ConfigurationSection section = itemSection.getConfigurationSection(key);
		    ItemStack item = getItemStack(section, useMatchMaterial);
		    if(item == null) continue;

		    double money = section.getDouble(value, def);
		    toRet.put(item, money);
	    }
	    return toRet;
	}*/
	
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
