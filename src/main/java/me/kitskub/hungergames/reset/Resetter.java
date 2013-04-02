package me.kitskub.hungergames.reset;

import me.kitskub.hungergames.games.HungerGame;

/**
 *
 *
 */
public abstract class Resetter {
	public abstract void init();
	
	public abstract boolean resetChanges(HungerGame game);
	
	public abstract void beginGame(HungerGame game);
}
