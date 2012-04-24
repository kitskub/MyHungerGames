package com.randude14.hungergames.games;

public class PlayerStat {
	private boolean dead;
	private int kills;
	
	public PlayerStat() {
		dead = false;
		kills = 0;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void kill() {
		kills++;
	}
	
	public int getKills() {
		return kills;
	}
	
	public boolean isDead() {
		return dead;
	}

}
