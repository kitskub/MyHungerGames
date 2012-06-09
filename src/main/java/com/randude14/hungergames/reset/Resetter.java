package com.randude14.hungergames.reset;

import com.randude14.hungergames.games.HungerGame;

/**
 *
 *
 */
public abstract class Resetter {
	public abstract void init();
	
	public abstract boolean resetChanges(HungerGame game);
	
	public abstract void beginGame(HungerGame game);
}
