package com.randude14.hungergames.api.event;

import com.randude14.hungergames.api.Game;
import org.bukkit.event.HandlerList;

public class GameRemoveEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();

	public GameRemoveEvent(final Game game) {
		super(game);
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
