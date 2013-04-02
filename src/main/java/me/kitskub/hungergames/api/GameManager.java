package me.kitskub.hungergames.api;

import me.kitskub.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class GameManager {
	public abstract Game createGame(String name);

	public abstract Game createGame(String name, String setup);

	public abstract boolean removeGame(String name);

	public abstract <T extends Game> List<EquatableWeakReference<T>> getGames();

	public abstract List<? extends Game> getRawGames();

	public abstract EquatableWeakReference<? extends Game> getGame(String name);

	public abstract Game getRawGame(String name);

	/**
	 * This does not care about whether the player is actually playing the game or not.
	 * This also does not care about whether a game is running
	 * If the player has the potential to rejoin, and therefore has lives, that is the game returned.
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public abstract WeakReference<? extends Game> getSession(Player player);

	public abstract Game getRawSession(Player player);
	
	public abstract void saveGame(Game game);

	public abstract void saveGames();
	/**
	 * This returns the game a player is playing. If the player is in a game, but not playing, returns null
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public abstract WeakReference<? extends Game> getPlayingSession(Player player);
	
	public abstract Game getRawPlayingSession(Player player);

	public abstract boolean doesNameExist(String name);

	public abstract boolean addSponsor(Player player, Player playerToBeSponsored);

	public abstract boolean addSpectator(Player player, Game game, Player spectated);

	public abstract EquatableWeakReference<? extends Game> getSpectating(Player player);

	public abstract boolean removeSpectator(Player player);

	public abstract void freezePlayer(Player player);

	public abstract void unfreezePlayer(Player player);

	public abstract boolean isPlayerFrozen(Player player);

	public abstract Location getFrozenLocation(Player player);

	public abstract <T extends Game> boolean isPlayerSubscribed(Player player, T game);

	public abstract <T extends Game> void removedSubscribedPlayer(Player player, T game);

	public abstract <T extends Game> void addSubscribedPlayer(Player player, T game);
}
