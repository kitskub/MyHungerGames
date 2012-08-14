package com.randude14.hungergames.stats;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import java.util.*;

import org.bukkit.entity.Player;

public class PlayerStat implements Comparable<PlayerStat> {
	public static Map<String, Map<HungerGame, PlayerStat>> stats = new HashMap<String, Map<HungerGame, PlayerStat>>();
	public static final String NODODY = "NOBODY";
	private Player player;
	private HungerGame game;
	private List<String> deaths;
	private List<String> kills;
	private PlayerState state;
	private long elapsedTimeInMillis;
	
	public static PlayerStat create(HungerGame game, Player player) {
		PlayerStat stat = new PlayerStat(game, player);
		if (stats.get(player.getName()) == null) stats.put(player.getName(), new HashMap<HungerGame, PlayerStat>());
		stats.get(player.getName()).put(game, stat);
		return stat;
	}

	public static HungerGame getGame(Player player) {
		if (stats.get(player.getName()) != null) {
			for (HungerGame gameGotten : stats.get(player.getName()).keySet()) {
				PlayerStat stat = stats.get(player.getName()).get(gameGotten);
				if (stat != null && stat.getState() != PlayerState.DEAD && stat.getState() != PlayerState.NOT_IN_GAME) return gameGotten;
			}
		}
		return null;
	}
	
	public static HungerGame getPlayingGame(Player player) {
		if (stats.get(player.getName()) != null) {
			for (HungerGame gameGotten : stats.get(player.getName()).keySet()) {
				PlayerStat stat = stats.get(player.getName()).get(gameGotten);
				if (stat != null && stat.getState() == PlayerState.PLAYING) return gameGotten;
			}
		}
		return null;
	}

	private PlayerStat(HungerGame game, Player player) {
		deaths = new ArrayList<String>();
		kills = new ArrayList<String>();
		this.player = player;
		this.game = game;
		state = PlayerState.NOT_IN_GAME;
		elapsedTimeInMillis = 0;
	}
	
	public void kill(String player) {
		kills.add(player);
	}
	
	public void death(String player) {
		deaths.add(player);
		update();
	}
	
	public void die() {
		state = PlayerState.DEAD;
	}
	
	public List<String> getKills() {
		return Collections.unmodifiableList(kills);
	}
	
	public int getNumKills() {
		return kills.size();
	}
	
	public List<String> getDesths() {
		return deaths;
	}
	
	public int getNumDeaths() {
		return deaths.size();
	}
	
	private void update() {
		if (state == PlayerState.DEAD) return;
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if (lives == 0 || deaths.size() >= lives) {
			die();
		}
	}
	
	public int getLivesLeft() {
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if(lives == 0) return -1;
		return lives - deaths.size();
	}
	
	public void setState(PlayerState state) {
		this.state = state;
	}
	
	public PlayerState getState() {
		return state;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void addTime(long time) {
		elapsedTimeInMillis += time;
	}
	
	public long getTime() {
		return elapsedTimeInMillis;
	}
	
	public static void clearGamesForPlayer(String player, HungerGame game) {
		stats.get(player).remove(game);
	}

	public int compareTo(PlayerStat o) {
		double ratio = kills.size() / (deaths.size() + 1);
		double otherRatio = o.kills.size() / (o.deaths.size() + 1);
		if (ratio == otherRatio) return 0;
		else if (ratio > otherRatio) return 1;
		return -1;
	}

	public enum PlayerState {
		NOT_IN_GAME,
		PLAYING,
		NOT_PLAYING,
		GAME_PAUSED,
		DEAD,
		WAITING;
	}
	
	public static class StatComparator implements Comparator<PlayerStat> {

		public int compare(PlayerStat o1, PlayerStat o2) {
			return o1.compareTo(o2);
		}
		
	}
}
