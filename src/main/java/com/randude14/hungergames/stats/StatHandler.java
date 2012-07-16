package com.randude14.hungergames.stats;

import com.randude14.hungergames.Logging;
import java.io.IOException;
import java.sql.Date;
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
	public final static String url = "";
	
	public static void updateStat(PlayerStat stat) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("authId", "");
		map.put("requestType", "update");
		map.put("playerName", stat.getPlayer().getName());
		map.put("lastLogin", new Date(System.currentTimeMillis()).toString()); // TODO better
		String wins;
		if (stat.hasRunOutOfLives()) {
			wins = "0";
		}
		else {
			wins = "1";
		}
		map.put("wins", wins);
		map.put("kills", String.valueOf(stat.getNumKills()));
		map.put("deaths", String.valueOf(stat.getNumKills()));
		try {
			ConnectionUtils.post(url, map);
		} catch (IllegalStateException ex) {
			Logging.debug("Error when updating stat: " + ex.getMessage());
		} catch (IOException ex) {
			Logging.debug("Error when updating stat: " + ex.getMessage());
		}
	}

}
