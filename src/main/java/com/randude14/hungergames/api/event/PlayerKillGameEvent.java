package com.randude14.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a player kills another player
public class PlayerKillGameEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player killer, killed;
	
	public PlayerKillGameEvent(final HungerGame game, final Player killer, final Player killed) {
		super(game);
		this.killer = killer;
		this.killed = killed;
	}
	
	public PlayerKillGameEvent(final HungerGame game, final Player killed) {
		this(game, null, killed);
	}
	
	public Player getKiller() {
		return killer;
	}
	
	public Player getKilled() {
		return killed;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
