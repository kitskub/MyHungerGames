package com.randude14.hungergames;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.randude14.hungergames.games.HungerGame;

public class GameManager implements Listener {
	private static final Plugin plugin = Plugin.getInstance();
	private static Set<HungerGame> games = new TreeSet<HungerGame>();
	private static CustomYaml yaml = new CustomYaml(new File(plugin.getDataFolder(), "games.yml"));

	public static boolean createGame(String name) {
		HungerGame game = new HungerGame(name);
		boolean attempt = games.add(game);
		if(attempt){
		    saveGames();
		}
		return attempt;
	}

	public static boolean removeGame(String name) {
	    HungerGame game = getGame(name);
	    if(game == null) return false;
	    boolean attempt = games.remove(game);
	    if(attempt){
		saveGames();
	    }
	    return attempt;
	}

	public static Set<HungerGame> getGames() {
		return games;
	}

	public static HungerGame getGame(String name) {
		for (HungerGame game : games) {
			if (game.getName().equals(name)) {
				return game;
			}

		}
		return null;
	}

	public static HungerGame getSession(Player player) {
		for (HungerGame game : games) {
			if (game.contains(player)) {
				return game;
			}

		}
		return null;
	}

	public static boolean doesNameExist(String name) {
		return getGame(name) != null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public static void playerKilled(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		HungerGame gameKilled = getSession(killed);
		Plugin.info(1 + "");
		if (gameKilled == null) {
			return;
		}
		Player killer = killed.getKiller();

		if (killer != null) {
			HungerGame gameKiller = getSession(killer);

			if (gameKilled.equals(gameKiller)) {
				String mess = Config.getKillMessage()
						.replace("<killer>", killer.getName())
						.replace("<killed>", killed.getName())
						.replace("<game>", gameKiller.getName());
				event.setDeathMessage(ChatColor.GREEN + mess);
				gameKiller.killed(killer, killed);
			}

		}

		else {
			gameKilled.killed(killed);
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void playerQuit(PlayerQuitEvent event) {
		playerLeft(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public static void playerKick(PlayerKickEvent event) {
		playerLeft(event.getPlayer());
	}

	private static void playerLeft(Player player) {
		HungerGame game = getSession(player);
		if (game == null) {
			return;
		}
		game.leave(player);
		String mess = Config.getLeaveMessage();
		mess = mess.replace("<player>", player.getName()).replace("<game>",
				game.getName());
		Plugin.broadcast(mess);
	}

	public static void loadGames() {
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection gamesSection = config
				.getConfigurationSection("games");
		if (gamesSection == null) {
			return;
		}
		games.clear();
		for (String name : gamesSection.getKeys(false)) {
			ConfigurationSection gameSection = gamesSection
					.getConfigurationSection(name);
			HungerGame game = new HungerGame(name);
			game.load(gameSection);
			games.add(game);
		}
		
	}

	public static void saveGames() {
		FileConfiguration config = yaml.getConfig();
		ConfigurationSection section = config.createSection("games");
		for (HungerGame game : games) {
			ConfigurationSection saveSection = section.createSection(game
					.getName());
			game.save(saveSection);
		}
		yaml.save();
	}

}
