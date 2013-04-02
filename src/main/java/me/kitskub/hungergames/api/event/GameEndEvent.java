package me.kitskub.hungergames.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.stats.PlayerStat.Team;

// called when a Hunger Game ends
public class GameEndEvent extends GameEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player winningPlayer;
	private final Team winningTeam;
	private final boolean finished;
	
	public GameEndEvent(final HungerGame game, final Team team) {
		super(game);
		winningPlayer = null;
		winningTeam = team;
		finished = true;
		
	}
	
	public GameEndEvent(final HungerGame game, final Player player) {
		super(game);
		winningPlayer = player;
		winningTeam = null;
		finished = true;
		
	}

	public GameEndEvent(HungerGame game, boolean finished) {
		super(game);
		this.finished = finished;
		this.winningPlayer = null;
		this.winningTeam = null;
	}
	
	public Player getWinningPlayer() {
		return winningPlayer;
	}
	
	public Team getWinningTeam() {
		return winningTeam;
	}
	
	public boolean isFinished() {
		return finished;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
