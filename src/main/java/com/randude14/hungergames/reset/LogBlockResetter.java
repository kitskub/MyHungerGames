package com.randude14.hungergames.reset;

import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;

import de.diddiz.LogBlock.CommandsHandler.CommandRollback;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LogBlockResetter extends Resetter{
    private LogBlock plugin;
    private Map<HungerGame, Long> timeStarted;

    @Override
    public void init() {
	timeStarted = new HashMap<HungerGame, Long>();
	plugin = (LogBlock) Bukkit.getPluginManager().getPlugin("LogBlock");
    }
    
    @Override
    public void beginGame(HungerGame game) {
	timeStarted.put(game, System.nanoTime());
    }

    @Override
    public boolean resetChanges(HungerGame game) {
	QueryParams params = new QueryParams(plugin);
	for (Player p : game.getAllPlayers()) params.setPlayer(p.getName());
	params.bct = BlockChangeType.ALL;
	params.since = (int) ((System.nanoTime() - timeStarted.get(game))/(60*1000));
	timeStarted.remove(game);
	params.needDate = true;
	params.needType = true;
	params.needData = true;
	params.needPlayer = true;
	params.silent = true;
	try {
	    CommandRollback commandRollback = plugin.getCommandsHandler().new CommandRollback(Bukkit.getConsoleSender(), params, true);
	} catch (Exception e) {
	    Logging.log(Level.SEVERE, "LogBlock resetting failed");
	    return false;
	}
	return true;
    }
}
