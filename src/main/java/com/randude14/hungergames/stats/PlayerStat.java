package com.randude14.hungergames.stats;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.games.HungerGame;

import java.util.*;

import org.bukkit.entity.Player;

public class PlayerStat implements Comparable<PlayerStat> {
	public static final String NODODY = "NOBODY";
	private LocalPlayer player;
	private HungerGame game;
	private List<String> deaths;
	private List<String> kills;
	private PlayerState state;
	private long elapsedTimeInMillis;

	public PlayerStat(HungerGame game, LocalPlayer player) {
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

	public LocalPlayer getPlayer() {
		return player;
	}
	
	public void addTime(long time) {
		elapsedTimeInMillis += time;
	}
	
	public long getTime() {
		return elapsedTimeInMillis;
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
