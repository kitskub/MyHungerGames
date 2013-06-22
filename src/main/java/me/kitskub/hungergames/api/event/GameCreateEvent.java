package me.kitskub.hungergames.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameCreateEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String game;
	

	public GameCreateEvent(final String game) {
		this.game = game;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean isCancelled) {
		cancelled = isCancelled;
	}
}
