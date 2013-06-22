package me.kitskub.hungergames.api.event;

import me.kitskub.hungergames.api.Game;
import org.bukkit.event.HandlerList;

public class GameCreatedEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	

	public GameCreatedEvent(final Game game) {
		super(game);
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
