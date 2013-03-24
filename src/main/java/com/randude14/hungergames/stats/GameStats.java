package com.randude14.hungergames.stats;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import com.randude14.hungergames.utils.ConnectionUtils;

public class GameStats {

	private HungerGame game;
	private List<Death> deaths = new ArrayList<Death>();
	private List<PlayerStat> players = new ArrayList<PlayerStat>();
	Map<String, String> map = new HashMap<String, String>();
	
	public GameStats(HungerGame game){
		this.game = game;
		map = new HashMap<String, String>();
	}
	
	public void saveGameData(){
		saveGameData(this.game);
	}
	
	public void saveGameData(HungerGame game){
		map.put("requestType", "updateGameDetails");
		map.put("startTime", String.valueOf(game.getInitialStartTime()));
		
		map.put("totalPlayers", String.valueOf(game.getAllPlayers().size()));
		if (game.getRemainingPlayers().size() != 1) {
			map.put("winner", "N/A");
		}
		else {
			map.put("winner", game.getRemainingPlayers().get(0).getName());
		}
	}
	
	public void addPlayer(PlayerStat playerStat){
		players.add(playerStat);
	}
	
	public void addAllPlayer(Collection<PlayerStat> playerStats){
		players.addAll(playerStats);
	}
	
	public void addDeath(String player, String killer, String cause){
		deaths.add(new Death(player, killer, cause,  System.currentTimeMillis() / 1000));
	}
	
	public void addDeath(Death death){
		deaths.add(death);
	}
	
	public void submit(){
		String urlString = Defaults.Config.WEBSTATS_IP.getGlobalString();
		if ("0.0.0.0".equals(urlString)) return;
		for (PlayerStat p : players) {
			String k = "players[" + p.getPlayer().getName() + "]";
			map.put(k + "[wins]", p.getState() == PlayerState.DEAD ? "0" : "1");
			map.put(k + "[death]", String.valueOf(p.getNumDeaths()));
			map.put(k + "[kills]", String.valueOf(p.getNumKills()));		
		}
		int j = 0;
		for (Death d : deaths) {
			String k = "deaths[" + (j++) + "]";
			map.put(k + "[time]", String.valueOf(d.getTime()));
			map.put(k + "[player]", d.getPlayer());
			map.put(k + "[killer]", d.getKiller() == null ? "" : d.getKiller());	
			map.put(k + "[cause]", d.getCause());		
		}
		long totalDuration = 0;
		for (int i = 0; i < Math.min(game.getEndTimes().size(), game.getStartTimes().size()); i++) {
			totalDuration += game.getEndTimes().get(i) - game.getStartTimes().get(i);
		}
		map.put("totalDuration", String.valueOf(totalDuration));
		
		try {
			ConnectionUtils.post(urlString, map);
		} catch (ParserConfigurationException ex) {
			Logging.debug("Error when updating games: " + ex.getMessage());
		} catch (SAXException ex) {
		} catch (IOException ex) {
			Logging.debug("Error when updating games: " + ex.getMessage());
		}
	}
	
	public static class Death{
		private String player;
		private String killer;
		private String cause;
		private long time;
		
		public Death(){
			
		}
		
		public Death(String player, String killer, String cause, long time) {
			this.player = player;
			this.killer = killer;
			this.cause = cause;
			this.time = time;
		}

		public String getPlayer() {
			return player;
		}
		public void setPlayer(String player) {
			this.player = player;
		}
		public String getKiller() {
			return killer;
		}
		public void setKiller(String killer) {
			this.killer = killer;
		}
		public String getCause() {
			return cause;
		}
		public void setCause(String cause) {
			this.cause = cause;
		}
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		
		
		
	}
	
}