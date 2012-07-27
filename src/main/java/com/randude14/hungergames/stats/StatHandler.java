package com.randude14.hungergames.stats;

import com.randude14.hungergames.utils.ConnectionUtils;
import com.randude14.hungergames.Config;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/*
* Players
*   name
*   lastLogin
*   totalGames
*   wins
*   kills
*   deaths
* Kills
*   killer
*   kill
*   day
*/
public class StatHandler {
	public static void updateGame(HungerGame game) {
		String urlString = Config.getWebStatsIP();
		if ("0.0.0.0".equals(urlString)) return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestType", "updateGames");
		map.put("startTime", String.valueOf(game.getInitialStartTime()));

		map.put("totalPlayers", String.valueOf(game.getAllPlayers().size()));
		if (game.getRemainingPlayers().size() != 1) {
			map.put("winner", "N/A");

		}
		else {
			map.put("winner", game.getRemainingPlayers().get(0).getName());
		}
		StringBuilder playersSB = new StringBuilder();
		for (String s : game.getAllPlayers()) {
			playersSB.append("{").append(s).append("}");
		}
		map.put("players", playersSB.toString());
		long totalDuration = 0;
		for (int i = 0; i < Math.min(game.getEndTimes().size(), game.getStartTimes().size()); i++) {
			totalDuration += game.getEndTimes().get(i) - game.getStartTimes().get(i);
		}
		map.put("totalDuration", String.valueOf(totalDuration));
		StringBuilder sponsorsSB = new StringBuilder();
		for (String s : game.getSponsors().keySet()) {
			sponsorsSB.append("{").append(s).append(":");
			for (String sponsee : game.getSponsors().get(s)) sponsorsSB.append("{").append(sponsee).append("}");
			sponsorsSB.append("}");
		}
		map.put("sponsors", sponsorsSB.toString());
		
		try {
			ConnectionUtils.post(urlString, map);
		} catch (IllegalStateException ex) {
			Logging.debug("Error when updating games: " + ex.getMessage());
		} catch (IOException ex) {
			Logging.debug("Error when updating games: " + ex.getMessage());
		}
	}

	public static void updateStat(PlayerStat stat) {
		String urlString = Config.getWebStatsIP();
		if ("0.0.0.0".equals(urlString)) return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("requestType", "updatePlayers");
		map.put("playerName", stat.getPlayer().getName());
		map.put("lastLogin", new Date(System.currentTimeMillis()).toString()); // TODO do this better
		map.put("totalTime", new Time(stat.getTime()).toString());
		String wins;
		if (stat.getState() == PlayerState.DEAD) {
			wins = "0";
		}
		else {
			wins = "1";
		}
		map.put("wins", wins);
		map.put("kills", String.valueOf(stat.getNumKills()));
		map.put("deaths", String.valueOf(stat.getNumKills()));
		try {
			ConnectionUtils.post(urlString, map);
		} catch (IllegalStateException ex) {
			Logging.debug("Error when updating stat: " + ex.getMessage());
		} catch (IOException ex) {
			Logging.debug("Error when updating stat: " + ex.getMessage());
		}
	}

}
