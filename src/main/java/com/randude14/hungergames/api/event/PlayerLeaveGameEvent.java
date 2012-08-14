package com.randude14.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.OfflinePlayer;

import com.randude14.hungergames.games.HungerGame;


// called when a player leaves a game
public class PlayerLeaveGameEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final OfflinePlayer player;
	private Type type;
	
	public PlayerLeaveGameEvent(final HungerGame game, final Player player, Type type) {
		super(game);
		this.player = player;
		this.type = type;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Type getType() {
		return type;
	}

	public static enum Type {
		LEAVE,
		QUIT,
		KICK;
	}
}
