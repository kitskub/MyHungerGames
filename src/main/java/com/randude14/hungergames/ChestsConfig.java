package com.randude14.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ChestsConfig {
	private static final HungerGames plugin = HungerGames.getInstance();
	private static CustomYaml chestConfig = new CustomYaml(new File(HungerGames.getInstance().getDataFolder(), "chestconfig.yml"));
	
	public static void reload() {
		chestConfig.load();
	}
	
	public static Set<String> getFixedChests() {
		return chestConfig.getConfig().getConfigurationSection("chests").getKeys(false);
	}
	
	private static List<ItemStack> getFixedChest(String chest, Set<String> checked) {
		List<ItemStack> fixedChests = new ArrayList<ItemStack>();
		if (checked.contains(chest)) return fixedChests;
		fixedChests.addAll(readFixedChest(plugin.getConfig().getConfigurationSection("chests." + chest)));
		checked.add(chest);
		for (String parent : plugin.getConfig().getStringList("chests." + chest + ".inherits")) {
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
