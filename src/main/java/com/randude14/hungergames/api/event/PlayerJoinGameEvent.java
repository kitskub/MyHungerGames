package com.randude14.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.randude14.hungergames.games.HungerGame;

// called when a player joins a game
public class PlayerJoinGameEvent extends GameEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final boolean isRejoin;
	
	public PlayerJoinGameEvent(final HungerGame game, final Player player, boolean isRejoin) {
		super(game);
		this.player = player;
		this.isRejoin = isRejoin;
		cancelled = false;
	}
	
	public PlayerJoinGameEvent(final HungerGame game, final Player player) {
		this(game, player, false);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isRejoin() {
		return isRejoin;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
