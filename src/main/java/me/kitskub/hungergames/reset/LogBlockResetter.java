package me.kitskub.hungergames.reset;

import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.games.HungerGame;

import de.diddiz.LogBlock.CommandsHandler.CommandRollback;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LogBlockResetter extends Resetter{
    private LogBlock plugin;

    @Override
    public void init() {
	plugin = (LogBlock) Bukkit.getPluginManager().getPlugin("LogBlock");
    }
    
    @Override
    public void beginGame(HungerGame game) {
    }

    @Override
    public boolean resetChanges(HungerGame game) {
	QueryParams params = new QueryParams(plugin);
	for (String p : game.getAllPlayers()) params.setPlayer(p);
	params.bct = BlockChangeType.ALL;
	long endTime = game.getEndTime();
	long startTime = game.getStartTime();
	params.since = (int) ((endTime - startTime) / 1000*60);
	params.needDate = true;
	params.needType = true;
	params.needData = true;
	params.needPlayer = true;
	params.silent = true;
	try {
		for (World w : game.getWorlds()) {
			params.world = w;
			CommandRollback commandRollback = plugin.getCommandsHandler().new CommandRollback(Bukkit.getConsoleSender(), params, true);
		}		
	} catch (Exception e) {
	    Logging.log(Level.SEVERE, "LogBlock resetting failed");
	    return false;
	}
	return true;
    }
}
