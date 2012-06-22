package com.randude14.hungergames;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
	private static final Logger logger = Logger.getLogger("MyHungerGames");

	public static void log(Level level, String record) {
		logger.log(level, record);		
	}

	static {
		try {
			HungerGames instance = HungerGames.getInstance();
			instance.getDataFolder().mkdirs();
			File file = new File(instance.getDataFolder(), "myhungergames.log");
			if (!file.exists()) file.createNewFile();
			FileHandler handler = new FileHandler("plugins/HungerGames/myhungergames.log", true);
			logger.addHandler(handler);
			Logger parent = Logger.getLogger("Mincraft");
			logger.setParent(parent);
		} catch (IOException ex) {
		}

	}

}
