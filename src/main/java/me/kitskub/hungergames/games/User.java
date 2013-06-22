package me.kitskub.hungergames.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.games.User.GameInEntry.Type;
import me.kitskub.hungergames.stats.GameStats;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.stats.PlayerStat.PlayerState;
import me.kitskub.hungergames.stats.PlayerStat.Team;
import me.kitskub.hungergames.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class User {

	private static Map<String, User> users = new HashMap<String, User>();
	private Map<Game, PlayerStat> stats = new HashMap<Game, PlayerStat>();
	private String player;
	private Location back;
	private List<Game> subscribedGames = new ArrayList<Game>();
	private GameInEntry gameIn = GameInEntry.NONE;
	private InventorySave savedInv;
	private final List<PermissionAttachment> perms = new ArrayList<PermissionAttachment>();
	private Team team;
	private PlayerState state;
	private int previousHealth;
	private boolean couldFly;
	private boolean wasFlying;
	private GameMode previousGamemode;

	private User(String player) {
		this.player = player;
		this.state = PlayerState.NOT_IN_GAME;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	public static User get(Player player) {
		User user = users.get(player.getName());
		if (user == null) {
			user = new User(player.getName());
			users.put(player.getName(), user);
		}
		return user;
	}

	public PlayerStat getStat(Game game) {
		return stats.get(game);
	}

	public boolean isBackSet() {
		return back != null;
	}

	public void setBackLocation() {
		back = getPlayer().getLocation();
	}

	public void clearBackLocation() {
		back = null;
	}

	public void goBack() {
		if (back != null) {
			//ChatUtils.send(player, "Teleporting you to your back location.");
			getPlayer().teleport(back);
			back = null;
		} else {
			ChatUtils.error(getPlayer(), "For some reason, there was no back location set. Did you already teleport back?");
		}
	}

	public boolean isSubscribed(Game game) {
		return subscribedGames.contains(game);
	}

	public void subscribe(Game game) {
		subscribedGames.add(game);
	}

	public boolean unsubscribe(Game game) {
		return subscribedGames.remove(game);
	}

	public GameInEntry getGameInEntry() {
		return gameIn;
	}

	public void setSavedInv(InventorySave save) {
		savedInv = save;
	}

	public InventorySave removeSavedInv() {
		InventorySave save = savedInv;
		savedInv = null;
		return save;
	}

	public boolean setGameIn(HungerGame game, Type type) {
		if (type == Type.NONE) {
			throw new IllegalArgumentException("Cannot join a game with Type NONE!");
		}
		gameIn = new GameInEntry(game, type);
		if (type == Type.PLAYING) {
			stats.put(game, new PlayerStat(game, this));//TODO this?
		}
		return true;
	}

	public boolean leaveGame() {
		GameInEntry previous = gameIn;
		gameIn = GameInEntry.NONE;
		if (previous.in != null) {
			stats.remove(previous.in);
			state = PlayerState.NOT_IN_GAME;
			if (previous.type == Type.PLAYING) {
				previous.in.quit(getPlayer(), true);
			} else if (previous.type == Type.SPECTATING) {
				((HungerGame)previous.in).removeSpectator(getPlayer());
			}
		}
		return true;
	}

	public static class GameInEntry {

		public static final GameInEntry NONE = new GameInEntry(null, Type.NONE);
		private final Game in;
		private final Type type;

		public enum Type {

			NONE,
			PLAYING,
			SPECTATING;
		}

		private GameInEntry(Game in, Type type) {
			this.in = in;
			this.type = type;
		}

		public Game getGame() {
			return in;
		}

		public Type getType() {
			return type;
		}
	}

	public Map<Game, PlayerStat> getStat() {
		return Collections.unmodifiableMap(stats);
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public PlayerState getState() {
		return state;
	}

	public void setPreviousHealth(int health) {
		previousHealth = health;
	}

	public int getPreviousHealth() {
		return previousHealth;
	}

	public void setPreviousGamemode(GameMode gamemode) {
		previousGamemode = gamemode;
	}

	public void setCouldFly(boolean b) {
		couldFly = b;
	}

	public void setWasFlying(boolean b) {
		wasFlying = b;
	}

	public boolean wasFlying() {
		return wasFlying;
	}

	public boolean couldFly() {
		return couldFly;
	}

	public GameMode getPreviousGamemode() {
		return previousGamemode;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + (this.player != null ? this.player.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if (this.player != other.player && (this.player == null || !this.player.equals(other.player))) {
			return false;
		}
		return true;
	}
}
