package com.randude14.hungergames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Files {
	CONFIG("config.yml", FileType.YML, true),
	ITEMCONFIG("itemconfig.yml", FileType.YML, true),
	GAMES("games.yml", FileType.YML, false),
	LANG("lang.yml", FileType.YML, true),
	LOG("myhungergames.log", FileType.LOG, false),
	SIGNS("signs.yml", FileType.YML, false);

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
					if (!file.exists()) HungerGames.getInstance().saveResource(path, false);
				}
				else {
					file.createNewFile();
				}
			}
			if (type == FileType.YML) {
				Logging.debug("Loading: " + path);
				yamlConfig = new YamlConfiguration();
				yamlConfig.load(file);
				if (hasDefault) {
					InputStream defConfigStream = HungerGames.getInstance().getResource(path);
					if (defConfigStream != null) {
						YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
						yamlConfig.setDefaults(defConfig);
					}
				}
			}
			else if (type == FileType.LOG) {
			}
		} catch (FileNotFoundException ex) {
			Logging.warning("Tried to create " + file.getName() + " but could not.");
		} catch (IOException ex) {
			Logging.warning("Something went wrong when loading: " + path);
		} catch (InvalidConfigurationException ex) {
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

	}
	
	public static void saveAll() {
		for (Files f : values()) {
			f.save();
		}
	}
}
