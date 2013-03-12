package com.randude14.hungergames.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a Hunger Game starts
public class GameStartEvent extends GameEvent implements Cancellable  {
	private static final HandlerList handlers = new HandlerList();
	private final boolean isResuming;
	
	public GameStartEvent(final HungerGame game, final boolean isResuming) {
		super(game);
		cancelled = false;
		this.isResuming = isResuming;
	}
	
	public GameStartEvent(final HungerGame game) {
		this(game, false);
	}
	
	public boolean isResuming() {
		return isResuming;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}


}
