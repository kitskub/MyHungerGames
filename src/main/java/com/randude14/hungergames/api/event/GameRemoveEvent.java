package com.randude14.hungergames.api.event;

import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

public class GameRemoveEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();

	public GameRemoveEvent(final HungerGame game) {
		super(game);
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
