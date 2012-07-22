package com.randude14.hungergames.stats;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import org.bukkit.entity.Player;

public class PlayerStat {
	public static final String NODODY = "NOBODY";
	private Player player;
	private List<String> deaths;
	private List<String> kills;
	private PlayerState state;
	private long elapsedTimeInMillis;
	
	public PlayerStat(Player player) {
		deaths = new ArrayList<String>();
		kills = new ArrayList<String>();
		this.player = player;
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
		HungerGame game = GameManager.getGame(player.getName());
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if (lives == 0 || deaths.size() >= lives) {
			die();
		}
	}
	
	public int getLivesLeft() {
		HungerGame game = GameManager.getGame(player.getName());
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
	
	public enum PlayerState {
		NOT_IN_GAME,
		PLAYING,
		NOT_PLAYING,
		GAME_PAUSED,
		DEAD,
		WAITING;
	}
}
