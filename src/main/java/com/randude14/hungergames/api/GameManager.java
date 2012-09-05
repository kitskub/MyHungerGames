package com.randude14.hungergames.api;

import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.core.Location;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class GameManager {
	public static final GameManager INSTANCE = com.randude14.hungergames.GameManager.INSTANCE;

	public abstract boolean createGame(String name);

	public abstract boolean createGame(String name, String setup);

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
	public abstract WeakReference<? extends Game> getSession(LocalPlayer player);

	public abstract Game getRawSession(LocalPlayer player);

	/**
	 * This returns the game a player is playing. If the player is in a game, but not playing, returns null
	 * 
	 * @param player
	 * @return the game a player is in
	 */
	public abstract WeakReference<? extends Game> getPlayingSession(LocalPlayer player);
	
	public abstract Game getRawPlayingSession(LocalPlayer player);

	public abstract boolean doesNameExist(String name);

	public abstract boolean addSponsor(LocalPlayer player, LocalPlayer playerToBeSponsored);

	public abstract boolean addSpectator(LocalPlayer player, Game game, LocalPlayer spectated);

	public abstract EquatableWeakReference<? extends Game> getSpectating(LocalPlayer player);

	public abstract boolean removeSpectator(LocalPlayer player);

	public abstract void freezePlayer(LocalPlayer player);

	public abstract void unfreezePlayer(LocalPlayer player);

	public abstract boolean isPlayerFrozen(LocalPlayer player);

	public abstract Location getFrozenLocation(LocalPlayer player);

	public abstract boolean isPlayerSubscribed(LocalPlayer player);

	public abstract void removedSubscribedPlayer(LocalPlayer player);

	public abstract void addSubscribedPlayer(LocalPlayer player);
}
