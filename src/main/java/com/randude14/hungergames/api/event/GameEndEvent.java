package com.randude14.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a Hunger Game ends
public class GameEndEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player winner;
	private final boolean finished;
	
	public GameEndEvent(final HungerGame game, final Player player) {
		super(game);
		winner = player;
		finished = true;
		
	}

	public GameEndEvent(HungerGame game) {
		super(game);
		this.finished = false;
		this.winner = null;
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
