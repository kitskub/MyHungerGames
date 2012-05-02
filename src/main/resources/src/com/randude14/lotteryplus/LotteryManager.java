package com.randude14.lotteryplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.util.TimeConstants;

public class LotteryManager extends Thread implements TimeConstants {

	private final Plugin plugin;
	private List<Lottery> lotteries;
	private FileConfiguration lotteryConfig;
	private File lotteryFile;
	private boolean reloading;

	protected LotteryManager(final Plugin plugin) {
		super("Lottery Manager");
		this.plugin = plugin;
		lotteries = new ArrayList<Lottery>();
		reloadConfig();
		if (!lotteryFile.exists()) {
			plugin.info("'lotteries.yml' was not found. Writing defaults.");
			saveDefaultConfig();
		}
		reloading = false;
	}

	public Lottery searchLottery(String name) {

		for (Lottery lottery : lotteries) {

			if (lottery.getName().equalsIgnoreCase(name)) {
				return lottery;
			}

		}

		return null;
	}

	public void run() {

		while (plugin.isEnabled()) {

			while (reloading) {
				pause(10L);
			}
			pause(1000L);
			for (int cntr = 0;cntr < lotteries.size();cntr++) {
				Lottery lottery = lotteries.get(cntr);
				if(!lottery.isRunByTime()) {
					continue;
				}
				try {
					lottery.countdown();
				} catch (Exception ex) {
				}

				if (lottery.isDrawing()) {
					long delay = Config.getTimeAfterDraws() + 3;
					while (delay > 0) {
						pause(1000L);
						delay--;
					}

				}
				
				if(!nameExists(lottery.getName())) {
					cntr--;
				}

			}

		}

	}

	private void pause(long milliseconds) {

		try {
			Thread.sleep(milliseconds);
		} catch (Exception ex) {
			plugin.warning("exception caught in pause() - " + ex);
		}

	}

	public void reloadConfig() {

		if (lotteryFile == null) {
			lotteryFile = new File(plugin.getDataFolder(), "lotteries.yml");
		}

		lotteryConfig = YamlConfiguration.loadConfiguration(lotteryFile);

		InputStream lotteryStream = plugin.getResource("lotteries.yml");

		if (lotteryStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(lotteryStream);
			lotteryConfig.setDefaults(defConfig);
		}

	}

	public void saveDefaultConfig() {
		plugin.saveResource("lotteries.yml", false);
	}

	public void saveConfig() {

		if (lotteryConfig == null || lotteryFile == null) {
			return;
		}

		try {
			lotteryConfig.save(lotteryFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save config to " + lotteryFile, ex);
		}

	}

	public ConfigurationSection getInfoSection(String lottery) {
		reloadConfig();
		return getConfig().getConfigurationSection("lotteries")
				.getConfigurationSection(lottery);
	}

	protected void loadLotteries() {

		try {
			ConfigurationSection lotterySection = getConfig()
					.getConfigurationSection("lotteries");
			ConfigurationSection saveSection = getConfig()
					.getConfigurationSection("saves");
			if(lotterySection == null) return;

			for (String lotteryName : lotterySection.getKeys(false)) {
				Lottery lottery = new Lottery(plugin, lotteryName);

				if (nameExists(lotteryName)) {
					continue;
				}

				if (saveSection != null && saveSection.contains(lotteryName)) {
					ConfigurationSection section = saveSection
							.getConfigurationSection(lotteryName);
					lottery.readSavedData(section);
				} else {
					ConfigurationSection section = lotterySection
							.getConfigurationSection(lotteryName);
					lottery.loadData(section);
				}

				lotteries.add(lottery);
				lottery.start();
			}

			plugin.info("lotteries loaded.");
		} catch (Exception ex) {
			ex.printStackTrace();
			plugin.warning("error has occured while loading lotteries in config.");
			plugin.abort();
		}

	}

	protected void reloadLotteries() {
		reloadConfig();
		reloading = true;

		try {
			ConfigurationSection lotterySection = getConfig()
					.getConfigurationSection("lotteries");

			for (String lotteryName : lotterySection.getKeys(false)) {
				if (nameExists(lotteryName)) {
					reloadLottery(lotteryName, true);
				} else {
					Lottery lottery = new Lottery(plugin, lotteryName);
					lotteries.add(lottery);
					reloadLottery(lotteryName, true);
					lottery.start();
				}
			}

			plugin.info("lotteries loaded.");
		} catch (Exception ex) {
			ex.printStackTrace();
			plugin.severe("error has occured while reloading lotteries in config.");
			plugin.abort();
		}
		reloading = false;
	}

	private void reloadLottery(String lotteryName, boolean flag) {
		Lottery lottery = searchLottery(lotteryName);

		if (lottery == null) {
			return;
		}
		if (!flag) {
			reloading = true;
		}
		reloadConfig();
		FileConfiguration config = getConfig();
		ConfigurationSection lotteriesSection = config
				.getConfigurationSection("lotteries");
		ConfigurationSection lotterySection = lotteriesSection
				.getConfigurationSection(lottery.getName());
		lottery.loadData(lotterySection);
		lottery.newSignFormatter();
		if (!flag) {
			reloading = false;
		}
	}

	public void reloadLottery(String lotteryName) {
		reloadLottery(lotteryName, false);
	}

	public void saveLotteries() {
		ConfigurationSection lotterySection = getConfig()
				.createSection("saves");
		for (Lottery lottery : lotteries) {
			lottery.save(lotterySection.createSection(lottery.getName()));
		}
		saveConfig();
	}

	public boolean nameExists(String name) {

		for (Lottery lottery : lotteries) {

			if (lottery.getName().equalsIgnoreCase(name)) {
				return true;
			}

		}

		return false;
	}

	public void removeLottery(String name) {

		for (int cntr = 0; cntr < lotteries.size(); cntr++) {
			Lottery lottery = lotteries.get(cntr);

			if (lottery.getName().equalsIgnoreCase(name)) {
				lotteries.remove(cntr);
			}

		}

	}

	public FileConfiguration getConfig() {

		if (lotteryConfig == null) {
			reloadConfig();
		}

		return lotteryConfig;
	}

	public List<Lottery> getLotteries() {
		return lotteries;
	}

}
