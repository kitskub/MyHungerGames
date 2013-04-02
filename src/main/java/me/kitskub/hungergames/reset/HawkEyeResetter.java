package me.kitskub.hungergames.reset;

import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.utils.Cuboid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RollbackCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

/**
 *
 *
 */
public class HawkEyeResetter extends Resetter{
	
	@Override
	public void init() {
	}

	@Override
	public void beginGame(HungerGame game) {
	}
    
	@Override
	public boolean resetChanges(HungerGame game) {
		SearchParser parser = new SearchParser();
		parser.players = game.getAllPlayers();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		parser.dateFrom = sdf.format(new Date(game.getInitialStartTime()));
		Set<String> worlds = new HashSet<String>();
		for (World w : game.getWorlds()) {
			worlds.add(w.getName());
		}
		parser.worlds = worlds.toArray(new String[worlds.size()]);
		HawkEyeAPI.performSearch(new RollbackCallback(new PlayerSession(new Logging.LogCommandSender("HawkEye")), RollbackType.GLOBAL), parser, SearchDir.DESC);
		parser.worlds = null;
		worlds.clear();
		for (Cuboid c : game.getCuboids()) {
			if (game.getWorlds().contains(c.getLower().getWorld())) continue;
			worlds.clear();
			worlds.add(c.getLower().getWorld().getName());
			parser.worlds = (String[]) worlds.toArray();
			parser.minLoc = c.getLower().toVector();
			parser.maxLoc = c.getUpper().toVector();
		}
		return true;
	}
}
