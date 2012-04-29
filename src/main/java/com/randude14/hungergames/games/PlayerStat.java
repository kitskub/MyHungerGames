package com.randude14.hungergames.games;

public class PlayerStat {
	private int deaths;
	private int kills;
	
	public PlayerStat() {
		deaths = 0;
		kills = 0;
	}
	
	public void kill() {
		kills++;
	}
	
	public void death() {
		deaths++;
	}
	
	public void die() {
	    deaths = 1; // TODO change to deaths >= getBleh(setupName)
	}
	
	public int getKills() {
		return kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public boolean hasRunOutOfLives() {
		return deaths >= 1; // TODO change to deaths >= getbleh(setupName)
	}

}
