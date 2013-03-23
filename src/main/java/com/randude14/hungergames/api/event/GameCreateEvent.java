package com.randude14.hungergames.api.event;

import com.randude14.hungergames.api.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameCreateEvent extends GameEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	

	public GameCreateEvent(final Game game) {
		super(game);
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
