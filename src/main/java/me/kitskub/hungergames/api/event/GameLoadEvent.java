package me.kitskub.hungergames.api.event;

import org.bukkit.event.HandlerList;

import me.kitskub.hungergames.games.HungerGame;

// called when a Hunger Game loads
public class GameLoadEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public GameLoadEvent(final HungerGame game) {
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
