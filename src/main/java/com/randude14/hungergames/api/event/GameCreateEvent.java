package com.randude14.hungergames.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

public class GameCreateEvent extends GameEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	

	public GameCreateEvent(final HungerGame game) {
		super(game);
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
