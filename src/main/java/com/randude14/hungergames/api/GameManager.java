package com.randude14.hungergames.api;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class GameManager {
	public static final GameManager INSTANCE = com.randude14.hungergames.GameManager.INSTANCE;

	public abstract boolean createGame(String name);

	public abstract boolean createGame(String name, String setup);

	public abstract boolean removeGame(String name);

	public abstract List<? extends Game> getGames();

	public abstract Game getGame(String name);

	/**
	 * This does not care about whether the player is actually playing the game or not.
	 * This also does not care about whether a game is running
	 * If the player has the potential to rejoin, and therefore has lives, that is the game returned.
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public abstract Game getSession(Player player);

	/**
	 * This returns the game a player is playing. If the player is in a game, but not playing, returns null
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public abstract Game getPlayingSession(Player player);

	public abstract boolean doesNameExist(String name);

	public abstract boolean addSponsor(Player player, Player playerToBeSponsored);

	public abstract boolean addSpectator(Player player, Game game, Player spectated);

	public abstract Game getSpectating(Player player);

	public abstract boolean removeSpectator(Player player);

	public abstract void freezePlayer(Player player);

	public abstract void unfreezePlayer(Player player);

	public abstract boolean isPlayerFrozen(Player player);

	public abstract Location getFrozenLocation(Player player);

	public abstract boolean isPlayerSubscribed(Player player);

	public abstract void removedSubscribedPlayer(Player player);

	public abstract void addSubscribedPlayer(Player player);
}
