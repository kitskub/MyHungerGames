package com.randude14.hungergames;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomYaml {
	private final FileConfiguration config = new YamlConfiguration();
	private final File file;
	
	public CustomYaml(final File file) {
		this.file = file;
	}
	
	public CustomYaml(String path) {
		this(new File(path));
	}
	
	public FileConfiguration getConfig() {
		load();
		return config;
	}

	private void load() {
		try {
		    config.load(file);
		} catch (Exception ex) {
		    Plugin.info(String.format("Could not load '%s'. If it has not been created, disregard this.", file.getName()));
		}
		
	}
	
	public void save() {
		try {
		    config.save(file);
		} catch (Exception ex) {
		    Plugin.warning(String.format("Could not save '%s'.", file.getName()));
		}
		
	}

}
