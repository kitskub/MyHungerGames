package com.randude14.hungergames.api.event;

import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a Hunger Game stops
public class GamePauseEvent extends GameEvent  {
	private static final HandlerList handlers = new HandlerList();
	
	public GamePauseEvent(final HungerGame game) {
		super(game);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
