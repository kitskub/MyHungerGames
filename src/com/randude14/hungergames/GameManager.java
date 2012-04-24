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
	private final Plugin plugin;
	private final Set<HungerGame> games;
	private CustomYaml yaml;

	public GameManager(final Plugin plugin) {
		this.plugin = plugin;
		this.games = new TreeSet<HungerGame>();
		yaml = new CustomYaml(new File(plugin.getDataFolder(), "games.yml"));
	}

	public boolean createGame(String name) {
		HungerGame game = new HungerGame(name);
		return games.add(game);
	}

	public boolean removeGame(String name) {
		return games.remove(name);
	}

	public Set<HungerGame> getGames() {
		return games;
	}

	public HungerGame getGame(String name) {
		for (HungerGame game : games) {
			if (game.equals(name)) {
				return game;
			}

		}
		return null;
	}

	public HungerGame getSession(Player player) {
		for (HungerGame game : games) {
			if (game.contains(player)) {
				return game;
			}

		}
		return null;
	}

	public boolean doesNameExist(String name) {
		return getGame(name) != null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerKilled(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		HungerGame gameKilled = getSession(killed);
		plugin.info(1 + "");
		if (gameKilled == null) {
			return;
		}
		Player killer = killed.getKiller();

		if (killer != null) {
			HungerGame gameKiller = getSession(killer);

			if (gameKilled.equals(gameKiller)) {
				String mess = plugin.getPluginConfig().getKillMessage()
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
	public void playerQuit(PlayerQuitEvent event) {
		playerLeft(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerKick(PlayerKickEvent event) {
		playerLeft(event.getPlayer());
	}

	private void playerLeft(Player player) {
		HungerGame game = getSession(player);
		if (game == null) {
			return;
		}
		game.leave(player);
		String mess = plugin.getPluginConfig().getLeaveMessage();
		mess = mess.replace("<player>", player.getName()).replace("<game>",
				game.getName());
		plugin.broadcast(mess);
	}

	public void loadGames() {
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

	public void saveGames() {
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
