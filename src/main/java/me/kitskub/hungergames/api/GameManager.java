package me.kitskub.hungergames.api;

import me.kitskub.hungergames.utils.EquatableWeakReference;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class GameManager {
	public abstract Game createGame(String name);

	public abstract Game createGame(String name, String setup);

	public abstract <T extends Game> List<EquatableWeakReference<T>> getGames();

	public abstract List<? extends Game> getRawGames();

	public abstract EquatableWeakReference<? extends Game> getGame(String name);

	public abstract Game getRawGame(String name);
	
	public abstract void saveGame(Game game);

	public abstract void saveGames();

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
