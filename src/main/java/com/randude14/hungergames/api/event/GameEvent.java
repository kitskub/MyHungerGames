package com.randude14.hungergames.api.event;

import org.bukkit.event.Event;

import com.randude14.hungergames.games.HungerGame;

public abstract class GameEvent extends Event {
	private final HungerGame game;
	
	public GameEvent(final HungerGame game) {
		this.game = game;
	}
	
	public HungerGame getGame() {
		return game;
	}

}