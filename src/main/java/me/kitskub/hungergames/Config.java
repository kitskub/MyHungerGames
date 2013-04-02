package me.kitskub.hungergames;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import static me.kitskub.hungergames.utils.ConfigUtils.*;


public class Config {

	public static List<ItemStack> getSpecialBlocksPlace(String setup) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (String s : Defaults.Config.SPECIAL_BLOCKS_PLACE.getStringList(setup)){
			list.add(getItemStack(s, 1, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		return list;
	}
	
	public static List<ItemStack> getSpecialBlocksBreak(String setup) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (String s :Defaults.Config.SPECIAL_BLOCKS_BREAK.getStringList(setup)){
			list.add(getItemStack(s, 1, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		return list;
	}
	
	public static List<ItemStack> getSpecialBlocksInteract(String setup) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (String s : Defaults.Config.SPECIAL_BLOCKS_INTERACT.getStringList(setup)){
			list.add(getItemStack(s, 1, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		return list;
	}

	// TODO use MatData, not ItemStack
	private static boolean getCanPlaceBlock(String setup, Block block, Set<String> checked) {
		boolean can = false;
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("setups." + setup + "." + "special-blocks-place")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		if (Files.CONFIG.getConfig().contains("setups." + setup + "." + "can-place-block")) {
			can |= Files.CONFIG.getConfig().getBoolean("setups." + setup + "." + "can-place-block") ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		}
		checked.add(setup);
		for (String parent : Files.CONFIG.getConfig().getStringList("setups." + setup + ".inherits")) {
			can |= getCanPlaceBlock(parent, block, checked);
		}
		return can;
	}
	public static boolean getCanPlaceBlock(String setup, Block block) {
		boolean can = false;
		can |= getCanPlaceBlock(setup, block, new HashSet<String>());
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("global.special-blocks-place")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		can |= Defaults.Config.CAN_PLACE_BLOCK.getGlobalBoolean() ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		return can;
	}

	private static boolean getCanBreakBlock(String setup, Block block, Set<String> checked) {
		boolean can = false;
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("setups." + setup + "." + "special-blocks-break")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		if (Files.CONFIG.getConfig().contains("setups." + setup + "." + "can-break-block")) {
			can |= Files.CONFIG.getConfig().getBoolean("setups." + setup + "." + "can-break-block") ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		}
		checked.add(setup);
		for (String parent : Files.CONFIG.getConfig().getStringList("setups." + setup + ".inherits")) {
			can |= getCanBreakBlock(parent, block, checked);
		}
		return can;
	}
	public static boolean getCanBreakBlock(String setup, Block block) {
		boolean can = false;
		can |= getCanBreakBlock(setup, block, new HashSet<String>());
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("global.special-blocks-break")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		can |= Defaults.Config.CAN_BREAK_BLOCK.getGlobalBoolean() ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		return can;
	}

	private static boolean getCanInteractBlock(String setup, Block block, Set<String> checked) {
		boolean can = false;
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("setups." + setup + "." + "special-blocks-interact")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		if (Files.CONFIG.getConfig().contains("setups." + setup + "." + "can-interact-block")) {
			can |= Files.CONFIG.getConfig().getBoolean("setups." + setup + "." + "can-interact-block") ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		}
		checked.add(setup);
		for (String parent : Files.CONFIG.getConfig().getStringList("setups." + setup + ".inherits")) {
			can |= getCanInteractBlock(parent, block, checked);
		}
		return can;
	}
	public static boolean getCanInteractBlock(String setup, Block block) {
		boolean can = false;
		can |= getCanInteractBlock(setup, block, new HashSet<String>());
		List<MatData> list = new ArrayList<MatData>();
		for (String s : Files.CONFIG.getConfig().getStringList("global.special-blocks-interact")){
			list.add(getMatData(s, Defaults.Config.USE_MATCH_MATERIAL.getGlobalBoolean()));
		}
		can |= Defaults.Config.CAN_INTERACT_BLOCK.getGlobalBoolean() ^ list.contains(new MatData(block.getType().getNewData(block.getData()), true));
		return can;
	}

	public static List<String> getSetups(){
	    ConfigurationSection section = Files.CONFIG.getConfig().getConfigurationSection("setups");
	    if(section == null) return Collections.emptyList();
	    List<String> list = (List<String>) section.getKeys(false);
	    return (list == null) ? new ArrayList<String>() : list;
	}
	
}