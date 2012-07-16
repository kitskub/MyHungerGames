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
	private boolean playing;
	private boolean dead;
	
	public PlayerStat(Player player) {
		deaths = new ArrayList<String>();
		kills = new ArrayList<String>();
		this.player = player;
		this.playing = false;
	}
	
	public void kill(String player) {
		kills.add(player);
	}
	
	public void death(String player) {
		deaths.add(player);
		if (hasRunOutOfLives()) dead = true;
	}
	
	public void die() {
		dead = true;
		playing = false;
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
	
	public boolean hasRunOutOfLives() {
		if (dead) return true;
		HungerGame game = GameManager.getGame(player.getName());
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if (lives == 0 || deaths.size() >= lives) {
			dead = true;
			return true;
		}
		return false;
	}
	
	public int getLivesLeft() {
		HungerGame game = GameManager.getGame(player.getName());
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if(lives == 0) return -1;
		return lives - deaths.size();
	}

	/**
	* @return true if player is currently "playing", disregarding lives
	*/
	public boolean isPlaying() {
		return playing;
	}

	/**
	* @param playing 
	*/
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public Player getPlayer() {
		return player;
	}
}
