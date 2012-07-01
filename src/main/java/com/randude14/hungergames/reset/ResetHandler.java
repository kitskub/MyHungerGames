package com.randude14.hungergames.reset;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.FileUtils;
import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ResetHandler{
    public static final String INTERNAL = "INTERNAL";
    public static final String LOGBLOCK = "LOGBLOCK";
    public static final String HAWKEYE = "HAWKEYE";
    private static Resetter resetter;
    
    
    public static void setRessetter(String string) {
	    if (string.equalsIgnoreCase(HAWKEYE)) {
		    resetter = new HawkEyeResetter();
	    }
	    else if (string.equalsIgnoreCase(LOGBLOCK)) {
		    resetter = new LogBlockResetter();
	    }
	    else {
		    resetter = new InternalResetter();
	    }
	    resetter.init();
    }
    
    public static void gameStarting(HungerGame game) {
	    resetter.beginGame(game);
    }
    
    private static boolean reloadWorld(String worldName) {
	// Unload
	World world = HungerGames.getInstance().getServer().getWorld(worldName); // At this point, all players SHOULD be out, so this is just a safety for anyone else
	if (world == null) return false;
	for (Player p : world.getPlayers()) {
		World spawn = Bukkit.getWorlds().get(0);
		if (spawn == world) spawn = Bukkit.getWorlds().get(1); // NullpointerException? It's possible
		p.teleport(spawn.getSpawnLocation()); // Really shouldn't get to this point, so nothing fancy
	}
	HungerGames.getInstance().getServer().unloadWorld(worldName, true);
	File serverWorlds = HungerGames.getInstance().getServer().getWorldContainer();
	File templateLoc = HungerGames.getInstance().getDataFolder();
	File worldFolder = new File(serverWorlds, worldName);
	File template = new File(templateLoc, worldName + "_template");
	if(!template.exists()) {
	    Logging.log(Level.WARNING, "There is no template world, so cancelling delete and reload.");
	    return false;
	}
	// Delete
	if (!FileUtils.deleteFolder(worldFolder) || worldFolder.exists()) {
	    // Couldn't delete it???
	    Logging.log(Level.SEVERE, "Couldn't delete a world!");
	    return false; // failed...
	}

	// Copy 
	if (!FileUtils.copyFolder(template, worldFolder)) {
	    // Dang
	    Logging.log(Level.SEVERE, "Couldn't copy a world!");
	    return false; // failed...
	}
	return true;
    }
    
    private static boolean resetBlockChanges(HungerGame game) {
	    if (!Config.getResetChanges(game.getSetup())) return true;
	    return resetter.resetChanges(game);
    }
    
    public static boolean resetChanges(HungerGame game) {
	if(Config.getReloadWorld(game.getSetup())) {
	    return reloadWorld(Config.getReloadWorldName(game.getSetup()));
	}
	return resetBlockChanges(game);
    }
}
