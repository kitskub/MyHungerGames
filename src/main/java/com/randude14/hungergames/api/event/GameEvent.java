package com.randude14.hungergames.api.event;

import com.randude14.hungergames.api.Game;

import org.bukkit.event.Event;

public abstract class GameEvent extends Event {
	private final Game game;
	
	public GameEvent(final Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}

}