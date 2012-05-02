package com.randude14.lotteryplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class LotteryExtras {

	private final Plugin plugin;
	private FileConfiguration materialConfig;
	private FileConfiguration enchantmentConfig;
	private FileConfiguration colorConfig;
	private File materialFile;
	private File enchantmentFile;
	private File colorsFile;

	public LotteryExtras(final Plugin plugin) {
		this.plugin = plugin;
		loadMaterialConfig();
		loadEnchantmentConfig();
		loadColorConfig();
	}

	private void loadMaterialConfig() {

		if (materialFile == null) {
			materialFile = new File(plugin.getDataFolder(), "items.yml");
		}

		materialConfig = YamlConfiguration.loadConfiguration(materialFile);

		InputStream materialStream = plugin.getResource("item.yml");

		if (materialStream != null) {
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(materialStream);
			config.setDefaults(materialConfig);
		}

	}

	private void loadEnchantmentConfig() {

		if (enchantmentFile == null) {
			enchantmentFile = new File(plugin.getDataFolder(),
					"enchantments.yml");
		}

		enchantmentConfig = YamlConfiguration
				.loadConfiguration(enchantmentFile);

		InputStream enchantmentStream = plugin.getResource("enchantments.yml");

		if (enchantmentStream != null) {
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(enchantmentStream);
			config.setDefaults(enchantmentConfig);
		}

	}

	private void loadColorConfig() {

		if (colorsFile == null) {
			colorsFile = new File(plugin.getDataFolder(), "colors.yml");
		}

		colorConfig = YamlConfiguration.loadConfiguration(colorsFile);

		InputStream colorsStream = plugin.getResource("colors.yml");

		if (colorsStream != null) {
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(colorsStream);
			config.setDefaults(colorConfig);
		}

	}

	public void writeMaterialConfig() {

		for (Material material : Material.values()) {
			materialConfig.createSection(material.name());
		}

		saveMaterialConfig();
	}

	public void writeEnchantmentConfig() {

		for (Enchantment enchantment : Enchantment.values()) {
			enchantmentConfig.set(
					enchantment.getName(),
					enchantment.getStartLevel() + "-"
							+ enchantment.getMaxLevel());
		}

		saveEnchantmentConfig();
	}

	public void writeColorConfig() {

		for (ChatColor color : ChatColor.values()) {
			colorConfig.set(color.name(), Character.valueOf(color.getChar()));
		}

		saveColorConfig();
	}

	private void saveMaterialConfig() {

		if (materialConfig == null || materialFile == null) {
			return;
		}

		try {
			materialConfig.save(materialFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save config to " + materialFile, ex);
		}

	}

	private void saveEnchantmentConfig() {

		if (enchantmentConfig == null || enchantmentFile == null) {
			return;
		}

		try {
			enchantmentConfig.save(enchantmentFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save config to " + enchantmentFile, ex);
		}

	}

	private void saveColorConfig() {

		if (colorConfig == null || colorsFile == null) {
			return;
		}

		try {
			colorConfig.save(colorsFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save config to " + colorsFile, ex);
		}

	}

}
