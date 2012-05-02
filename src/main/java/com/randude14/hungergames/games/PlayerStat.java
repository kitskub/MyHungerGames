package com.randude14.hungergames.games;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import org.bukkit.entity.Player;

public class PlayerStat {
	private Player player;
	private int deaths;
	private int kills;
	
	public PlayerStat(Player player) {
		deaths = 0;
		kills = 0;
		this.player = player;
	}
	
	public void kill() {
		kills++;
	}
	
	public void death() {
		deaths++;
	}
	
	public void die() {
	    HungerGame game = GameManager.getGame(player.getName());
	    deaths = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
	}
	
	public int getKills() {
		return kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public boolean hasRunOutOfLives() {
		HungerGame game = GameManager.getGame(player.getName());
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if(lives == 0) return true;
		return deaths >= lives;
	}
	
	public int getLivesLeft() {
		HungerGame game = GameManager.getGame(player.getName());
		int lives = (game == null) ? Config.getLivesGlobal() : Config.getLives(game.getSetup());
		if(lives == 0) return -1;
		return lives - deaths;
	}

}
