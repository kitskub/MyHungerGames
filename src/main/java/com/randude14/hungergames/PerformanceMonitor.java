package com.randude14.hungergames;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceMonitor {
	private static Map<String, Long> activities = new HashMap<String, Long>();
	private static Map<String, List<Long>> finished = new HashMap<String, List<Long>>();
	private static boolean profile;
	
	public static void startActivity(String name) {
		activities.put(name, System.currentTimeMillis());
	}
	
	public static void stopActivity(String name) {
		Long startTime = activities.get(name);
		if (startTime == null) return;
		if (finished.get(name) == null) finished.put(name, new ArrayList<Long>());
		finished.get(name).add((System.currentTimeMillis() - startTime)/1000);
	}
	
	public void saveFile() {
		if (!profile) return;
		File profiledFile = new File(HungerGames.getInstance().getDataFolder(), "profile.log");
		try {
			profiledFile.createNewFile();
			FileWriter writer = new FileWriter(profiledFile);
			for (String s : finished.keySet()) {
				writer.write(String.format("Profile for %s:\r\n", s));
				for (long l : finished.get(s)) writer.write(String.format("\tProfile time %d:\r\n", l));
			}
			
		} catch (IOException ex) {
			Logging.debug("Could not save profile file.");
		}
		
	}
	
	public static void on() {
		profile = true;
	}
}
