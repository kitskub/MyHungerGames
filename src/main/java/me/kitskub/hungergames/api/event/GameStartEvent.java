package me.kitskub.hungergames.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.kitskub.hungergames.games.HungerGame;

// called when a Hunger Game starts
public class GameStartEvent extends GameEvent implements Cancellable  {
	private static final HandlerList handlers = new HandlerList();
	
	public GameStartEvent(final HungerGame game) {
		super(game);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		super.setCancelled(isCancelled);
	}
}
