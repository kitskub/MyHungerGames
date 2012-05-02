package com.randude14.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a Hunger Game ends
public class GameEndEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player winner;
	
	public GameEndEvent(final HungerGame game, final Player player) {
		super(game);
		winner = player;
	}
	
	public GameEndEvent(final HungerGame game) {
		this(game, null);
	}
	
	public Player getWinner() {
		return winner;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
