package com.randude14.hungergames.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.randude14.hungergames.GameCountdown;
import com.randude14.hungergames.Plugin;

public class HungerGame implements Comparable<HungerGame> {
	private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final List<Location> spawnPoints;
	private final List<Location> chests;
	private final List<String> readyToPlay;
	private final String name;
	private final Plugin plugin;
	private Location spawn;
	private boolean isRunning;
	private boolean isCounting;
	private boolean enabled;

	public HungerGame(String name) {
		this.name = name;
		plugin = Plugin.getInstance();
		spawnPoints = new ArrayList<Location>();
		chests = new ArrayList<Location>();
		readyToPlay = new ArrayList<String>();
		spawnsTaken = new HashMap<String, Location>();
		stats = new HashMap<String, PlayerStat>();
		spawn = null;
		isRunning = isCounting = false;
		enabled = true;
	}

	public void load(ConfigurationSection section) {

		if (section.contains("spawn-points")) {
			ConfigurationSection spawnPointsSection = section
					.getConfigurationSection("spawn-points");
			for (String key : spawnPointsSection.getKeys(false)) {
				String str = spawnPointsSection.getString(key);
				Location loc = plugin.parseToLoc(str);
				if (loc == null) {
					plugin.warning(String.format(
							"failed to load location '%s'", str));
					continue;
				}
				spawnPoints.add(loc);
			}

		}

		if (section.contains("chests")) {
			ConfigurationSection chestsSection = section
					.getConfigurationSection("chests");
			for (String key : chestsSection.getKeys(false)) {
				String str = chestsSection.getString(key);
				Location loc = plugin.parseToLoc(str);
				if (loc == null) {
					plugin.warning(String.format(
							"failed to load location '%s'", str));
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					plugin.warning(String.format("'%s' is no longer a chest.",
							str));
					continue;
				}
				chests.add(loc);
			}

		}
		enabled = section.getBoolean("enabled", Boolean.TRUE);
		spawn = plugin.parseToLoc(section.getString("spawn"));
	}

	public void save(ConfigurationSection section) {
		if (!spawnPoints.isEmpty()) {
			ConfigurationSection spawnPointsSection = section
					.createSection("spawn-points");
			for (int cntr = 0; cntr < spawnPoints.size(); cntr++) {
				Location loc = spawnPoints.get(cntr);
				spawnPointsSection.set("spawnpoint" + (cntr + 1),
						plugin.parseToString(loc));
			}

		}

		if (!chests.isEmpty()) {
			ConfigurationSection chestsSection = section
					.createSection("chests");
			for (int cntr = 0; cntr < chests.size(); cntr++) {
				Location loc = chests.get(cntr);
				chestsSection.set("chest" + (cntr + 1),
						plugin.parseToString(loc));
			}

		}
		section.set("enabled", enabled);
		if (spawn != null) {
			section.set("spawn", plugin.parseToString(spawn));
		}

	}

	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof HungerGame) {
			HungerGame game = (HungerGame) obj;
			return compareTo(game) == 0;
		}

		if (obj instanceof String) {
			String str = (String) obj;
			return name.equalsIgnoreCase(str);
		}
		return false;
	}

	public int compareTo(HungerGame game) {
		return game.name.compareToIgnoreCase(name);
	}

	public boolean addReadyPlayer(Player player) {
		String name = player.getName();
		if (readyToPlay.contains(name)) {
			plugin.error(
					player,
					String.format("You have already cast your vote that you are ready to play."));
			return false;
		}

		else {
			if (!enabled) {
				plugin.error(player,
						String.format("%s is currently not enabled.", name));
				return false;
			}
			if (isCounting) {
				plugin.error(player,
						String.format("%s is already counting down.", name));
				return false;
			}
			if (isRunning) {
				plugin.error(player,
						String.format("%s is already running a game.", name));
				return false;
			}
			if (stats.size() < 2) {
				return false;
			}
			readyToPlay.add(name);
			int minVote = plugin.getPluginConfig().getMinVote();
			plugin.info("" + minVote);
			if (readyToPlay.size() >= minVote) {
				plugin.broadcast(String.format(
						"Everyone is ready to play %s. Starting game...",
						this.name));
				start(player);
			} else {
				String mess = plugin.getPluginConfig().getVoteMessage()
						.replace("<player>", player.getName())
						.replace("<game>", this.name);
				plugin.broadcast(mess);
			}
			return true;
		}

	}

	public boolean start(Player player, int ticks) {
		if (isRunning) {
			return false;
		}

		if (stats.size() < 2) {
			plugin.error(player,
					String.format("There are not enough players in %s", name));
			return false;
		}
		if (isCounting) {
			plugin.error(player,
					String.format("%s is already counting down.", name));
			return false;
		}
		if (!enabled) {
			plugin.error(player,
					String.format("%s is currently not enabled.", name));
			return false;
		}
		if (ticks <= 0) {
			plugin.broadcast(String.format("Starting %s. Go!!", name));
			startGame();
		} else {
			new GameCountdown(plugin, this, ticks);
			isCounting = true;
		}
		return true;
	}

	public boolean start(Player player) {
		return start(player, plugin.getPluginConfig().getDefaultTime());
	}

	public void startGame() {
		releasePlayers();
		fillChests();
		for (String p : stats.keySet()) {
			Player player = plugin.getServer().getPlayer(p);
			if (player == null) {
				continue;
			}
			World world = player.getWorld();
			if (world.getFullTime() != 0L) {
				world.setFullTime(0L);
			}
			player.setHealth(20);
			player.setFoodLevel(20);
		}
		isRunning = true;
		isCounting = false;
	}

	public void releasePlayers() {
		for (String p : stats.keySet()) {
			Player player = plugin.getServer().getPlayer(p);
			plugin.unfreezePlayer(player);
		}

	}

	public void fillChests() {
		for (int cntr = 0; cntr < chests.size(); cntr++) {
			Location loc = chests.get(cntr);
			Chest chest = (Chest) loc.getBlock().getState();
			plugin.fillChest(chest);
		}

	}

	public void setEnabled(boolean flag) {
		enabled = flag;
	}

	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}

	public synchronized boolean rejoin(Player player) {
		if (!stats.containsKey(player.getName())) {
			plugin.error(player,
					String.format("You are not in the game %s.", name));
			return false;
		}
		Random rand = plugin.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		spawnsTaken.remove(player.getName());
		while (spawnTaken(loc)) {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		}
		spawnsTaken.put(player.getName(), loc);
		stats.put(player.getName(), new PlayerStat());
		player.teleport(loc);
		if (!isRunning) {
			plugin.freezePlayer(player);
		}
		return true;
	}

	public synchronized boolean join(Player player) {
		if (stats.containsKey(player.getName())) {
			plugin.error(player, "You are already in this game.");
			return false;
		}
		if (!plugin.hasInventoryBeenCleared(player)) {
			plugin.error(
					player,
					"You must clear your inventory first (Be sure to check you're not wearing armor either).");
			return false;
		}
		if (spawnsTaken.size() >= spawnPoints.size()) {
			plugin.error(player, String.format("%s is already full.", name));
			return false;
		}
		if (!enabled) {
			plugin.error(player,
					String.format("%s is currently not enabled.", name));
			return false;
		}
		Random rand = plugin.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		while (spawnTaken(loc)) {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		}
		spawnsTaken.put(player.getName(), loc);
		stats.put(player.getName(), new PlayerStat());
		plugin.giveMultiversePermission(player);
		player.teleport(loc);
		if (!isRunning) {
			plugin.freezePlayer(player);
		}
		return true;
	}

	private boolean spawnTaken(Location loc) {

		for (Location comp : spawnsTaken.values()) {
			if (plugin.equals(loc, comp)) {
				return true;
			}

		}
		return false;
	}

	public synchronized boolean leave(Player player) {
		if (!stats.containsKey(player.getName())) {
			plugin.error(player,
					String.format("You are not in the game %s.", name));
			return false;
		}
		stats.remove(player.getName());
		readyToPlay.remove(player.getName());
		spawnsTaken.remove(player.getName());
		if (plugin.isPlayerFrozen(player)) {
			plugin.unfreezePlayer(player);
		}
		plugin.takeMultiversePermission(player);
		teleportPlayerToSpawn(player);
		if (isRunning) {
			checkForGameOver();
		}
		return true;
	}

	private void clear() {
		stats.clear();
		spawnsTaken.clear();
	}

	public void teleportPlayerToSpawn(Player player) {
		if (player == null) {
			return;
		}
		if (spawn != null) {
			player.teleport(spawn);
			plugin.send(player,
					String.format("Teleporting you to %s's spawn.", name));
		} else {
			plugin.error(
					player,
					String.format(
							"There was no spawn set for %s. Please contanct an admin for help.",
							name));
		}

	}

	public void checkForGameOver() {
		if (isOver()) {
			Player winner = getSurvivor();
			if (winner == null) {
				plugin.broadcast("strangly there was no winner left.");
			} else {
				plugin.broadcast(String.format(
						"%s has won the game %s! Congratulations!",
						winner.getName(), name));
				spawnsTaken.remove(winner);
				teleportPlayerToSpawn(winner);
				plugin.takeMultiversePermission(winner);
			}
			isRunning = false;
			clear();
		}

		else {
			List<String> remaining = new ArrayList<String>();
			for (String player : stats.keySet()) {
				if (!stats.get(player).isDead()) {
					remaining.add(player);
				}

			}

			String mess = "Remaining players: ";
			for (int cntr = 0; cntr < remaining.size(); cntr++) {
				mess += remaining.get(cntr);
				if (cntr < remaining.size() - 1) {
					mess += ", ";
				}

			}
			plugin.broadcastRaw(mess, ChatColor.WHITE);
		}

	}

	public String getInfo() {
		return String.format("%s[%d/%d]: %b", name, stats.size(),
				spawnPoints.size(), enabled);
	}

	public boolean contains(Player... players) {
		for (Player player : players) {
			if (!stats.containsKey(player.getName())) {
				return false;
			}

		}
		return true;
	}

	public void killed(Player killer, Player killed) {
		if (!isRunning || stats.get(killed).isDead()) {
			return;
		}
		PlayerStat killerStat = getPlayerStat(killer.getName());
		PlayerStat killedStat = getPlayerStat(killed.getName());
		killerStat.kill();
		killedStat.setDead(true);
		if (plugin.isPlayerFrozen(killed)) {
			plugin.unfreezePlayer(killed);
		}
		spawnsTaken.remove(killed.getName());
		plugin.takeMultiversePermission(killed);
		checkForGameOver();
	}

	public void killed(Player killed) {
		if (!isRunning || stats.get(killed).isDead()) {
			return;
		}
		PlayerStat killedStat = getPlayerStat(killed.getName());
		killedStat.setDead(true);
		if (plugin.isPlayerFrozen(killed)) {
			plugin.unfreezePlayer(killed);
		}
		spawnsTaken.remove(killed.getName());
		plugin.takeMultiversePermission(killed);
		checkForGameOver();
	}

	private Player getSurvivor() {
		for (String player : stats.keySet()) {
			PlayerStat stat = stats.get(player);
			if (!stat.isDead()) {
				return plugin.getServer().getPlayer(player);
			}

		}
		return null;
	}

	public boolean isOver() {
		int numAlive = 0;
		for (String player : stats.keySet()) {
			PlayerStat stat = stats.get(player);
			if (!stat.isDead()) {
				numAlive++;
			}

		}
		return numAlive < 2;
	}

	public PlayerStat getPlayerStat(String player) {
		return stats.get(player);
	}

	public void listStats(Player player) {
		plugin.send(player, String.format(
				"<name>[kills], %sGreen: alive, %sRed: dead",
				ChatColor.GREEN.toString(), ChatColor.RED.toString()));
		plugin.send(player, "");
		List<String> players = new ArrayList<String>(stats.keySet());
		for (int cntr = 0; cntr < stats.size(); cntr += 5) {
			String mess = "";
			for (int i = cntr; i < cntr + 5 && i < stats.size(); i++) {
				String p = players.get(i);
				PlayerStat stat = stats.get(p);
				ChatColor color = stat.isDead() ? ChatColor.RED
						: ChatColor.GREEN;
				mess += String.format("%s%s[%d]", color.toString(), p,
						stat.getKills());
				if (i < cntr + 4 && cntr < stats.size() - 1) {
					mess += ", ";
				}

			}
			plugin.send(player, mess);
		}

	}

	public String getName() {
		return name;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean addChest(Location loc) {
		for (Location l : chests) {
			if (plugin.equals(l, loc)) {
				return false;
			}

		}
		chests.add(loc);
		return true;
	}

	public boolean addSpawnPoint(Location loc) {
		for (Location l : spawnPoints) {
			if (plugin.equals(l, loc)) {
				return false;
			}

		}
		spawnPoints.add(loc);
		return true;
	}

	public boolean removeChest(Location loc) {
		Iterator<Location> iterator = chests.iterator();
		while (iterator.hasNext()) {
			if (plugin.equals(loc, iterator.next())) {
				iterator.remove();
				return true;
			}

		}
		return false;
	}

	public boolean removeSpawnPoint(Location loc) {
		Iterator<Location> iterator = spawnPoints.iterator();
		Location l = null;
		while (iterator.hasNext()) {
			if (plugin.equals(loc, l = iterator.next())) {
				iterator.remove();
				for (String p : spawnsTaken.keySet()) {
					Location comp = spawnsTaken.get(p);
					if (plugin.equals(l, comp)) {
						spawnsTaken.remove(p);
						Player player = plugin.getServer().getPlayer(p);
						if (player != null) {
							plugin.error(
									player,
									String.format(
											"Your spawn point has been recently removed. Try rejoining by typing '/hg join %s'",
											name));
							leave(player);
						}

					}

				}
				return true;
			}

		}
		return false;
	}

}
