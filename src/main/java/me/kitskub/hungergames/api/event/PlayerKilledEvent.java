package me.kitskub.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.kitskub.hungergames.games.HungerGame;

// called when a player kills another player
public class PlayerKilledEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player killer, killed;
	private String deathMessage;
	
	public PlayerKilledEvent(final HungerGame game,final Player killed, final Player killer) {
		super(game);
		this.killer = killer;
		this.killed = killed;
		deathMessage = null;
	}

	public PlayerKilledEvent(final HungerGame game, final Player killed) {
		this(game, killed, null);
	}

	public Player getKiller() {
		return killer;
	}
	
	public Player getKilled() {
		return killed;
	}
	
	public void setDeathMessage(String message) {
		deathMessage = message;
	}
	
	public String getDeathMessage() {
		return deathMessage;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
