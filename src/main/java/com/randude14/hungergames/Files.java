package com.randude14.hungergames;

import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.Bukkit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Files {
	CONFIG("config.yml", FileType.YML, true),
	ITEMCONFIG("itemconfig.yml", FileType.YML, true),
	GAMES("games.yml", FileType.YML, false),
	LANG("lang.yml", FileType.YML, true),
	LOG("myhungergames.log", FileType.LOG, false),
	SIGNS("signs.yml", FileType.YML, false),
	LOBBY_SIGNS("lobbysigns.yml", FileType.YML, false);

	private String path;
	private FileType type;
	private boolean hasDefault;
	private YamlConfiguration yamlConfig;

	private Files(String path, FileType type, boolean hasDefault) {
		this.path = path;
		this.type = type;
		this.hasDefault = hasDefault;
	}

	public static enum FileType {
		YML,
		LOG;
	}
	
	public void load() {
		File file = getFile();
		try {
			if (!file.exists()) {
				Logging.debug("File %s does not exist. Creating.", path);
				if (hasDefault) {
					HungerGames.getInstance().saveResource(path, false);
				}
				else {
					file.createNewFile();
				}
			}
			if (type == FileType.YML) {
				//Logging.debug("Loading: " + path);
				yamlConfig = new YamlConfiguration();
				yamlConfig.load(file);
			}
			else if (type == FileType.LOG) {
			}
		} catch (FileNotFoundException ex) {
			Logging.warning("Tried to create " + file.getName() + " but could not.");
		} catch (IOException ex) {
			Logging.warning("Something went wrong when loading: " + path);
		} catch (InvalidConfigurationException ex) {
			Logging.warning(ex.getMessage());
			ex.printStackTrace();
		}		
	}
	
	public File getFile() {
		return new File(HungerGames.getInstance().getDataFolder(), path);
	}
	
	public void save() {
		try {
			if (type == FileType.YML) {
				yamlConfig.save(getFile());

			}
			else if (type == FileType.LOG) {
			}
		} catch (IOException ex) {
			Logging.warning("Something went wrong when saving: " + path);
		}
	}
	
	public YamlConfiguration getConfig() {
		if (type != FileType.YML) {
			throw new IllegalStateException("This Files type is not a YML file!");
		}
		return yamlConfig;
	}

	public static void loadAll() {
		HungerGames.getInstance().getDataFolder().mkdirs();
		for (Files f : values()) {
			f.load();
		}
		update();
	}
	
	public static void saveAll() {
		for (Files f : values()) {
			f.save();
		}
	}
	
	private static void update() {
		if (ITEMCONFIG.getConfig().getConfigurationSection("global.chest-loot") != null) {
			ChatUtils.error(Bukkit.getConsoleSender(), "The itemconfig.yml configuration has changed! An example file has been copied to the MyHungerGames folder. Please update!");
			HungerGames.getInstance().saveResource("itemconfig_example.yml", false);
		}
	}
}
