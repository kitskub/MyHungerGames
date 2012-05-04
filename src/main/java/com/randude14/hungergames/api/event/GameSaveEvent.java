package com.randude14.hungergames.api.event;

import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a Hunger Game saves
public class GameSaveEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public GameSaveEvent(final HungerGame game) {
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
