package com.randude14.hungergames.games;

import com.randude14.hungergames.Config;
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
import org.bukkit.Bukkit;

public class HungerGame implements Comparable<HungerGame> {
	private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final List<Location> spawnPoints;
	private final List<Location> chests;
	private final List<String> readyToPlay;
	private final String name;
	private Location spawn;
	private boolean isRunning;
	private boolean isCounting;
	private boolean enabled;

	public HungerGame(String name) {
		this.name = name;
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
				Location loc = Plugin.parseToLoc(str);
				if (loc == null) {
					Plugin.warning(String.format(
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
				Location loc = Plugin.parseToLoc(str);
				if (loc == null) {
					Plugin.warning(String.format(
							"failed to load location '%s'", str));
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Plugin.warning(String.format("'%s' is no longer a chest.",
							str));
					continue;
				}
				chests.add(loc);
			}

		}
		enabled = section.getBoolean("enabled", Boolean.TRUE);
		spawn = Plugin.parseToLoc(section.getString("spawn"));
	}

	public void save(ConfigurationSection section) {
		if (!spawnPoints.isEmpty()) {
			ConfigurationSection spawnPointsSection = section
					.createSection("spawn-points");
			for (int cntr = 0; cntr < spawnPoints.size(); cntr++) {
				Location loc = spawnPoints.get(cntr);
				spawnPointsSection.set("spawnpoint" + (cntr + 1),
						Plugin.parseToString(loc));
			}

		}

		if (!chests.isEmpty()) {
			ConfigurationSection chestsSection = section
					.createSection("chests");
			for (int cntr = 0; cntr < chests.size(); cntr++) {
				Location loc = chests.get(cntr);
				chestsSection.set("chest" + (cntr + 1),
						Plugin.parseToString(loc));
			}

		}
		section.set("enabled", enabled);
		if (spawn != null) {
			section.set("spawn", Plugin.parseToString(spawn));
		}

	}

	@Override
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
		String playerName = player.getName();
		if (readyToPlay.contains(playerName)) {
			Plugin.error(
					player,
					String.format("You have already cast your vote that you are ready to play."));
			return false;
		}

		else {
			if (!enabled) {
				Plugin.error(player,
						String.format("%s is currently not enabled.", playerName));
				return false;
			}
			if (isCounting) {
				Plugin.error(player,
						String.format("%s is already counting down.", playerName));
				return false;
			}
			if (isRunning) {
				Plugin.error(player,
						String.format("%s is already running a game.", playerName));
				return false;
			}
			if (stats.size() < 2) {
				return false;
			}
			readyToPlay.add(playerName);
			int minVote = Config.getMinVote();
			Plugin.info("" + minVote);
			if (readyToPlay.size() >= minVote) {
				Plugin.broadcast(String.format(
						"Everyone is ready to play %s. Starting game...",
						this.name));
				start(player);
			} else {
				String mess = Config.getVoteMessage()
						.replace("<player>", player.getName())
						.replace("<game>", this.name);
				Plugin.broadcast(mess);
			}
			return true;
		}

	}

	public boolean start(Player player, int ticks) {
		if (isRunning) {
			return false;
		}

		if (stats.size() < 2) {
			Plugin.error(player,
					String.format("There are not enough players in %s", name));
			return false;
		}
		if (isCounting) {
			Plugin.error(player,
					String.format("%s is already counting down.", name));
			return false;
		}
		if (!enabled) {
			Plugin.error(player,
					String.format("%s is currently not enabled.", name));
			return false;
		}
		if (ticks <= 0) {
			Plugin.broadcast(String.format("Starting %s. Go!!", name));
			startGame();
		} else {
			new GameCountdown(this, ticks);
			isCounting = true;
		}
		return true;
	}

	public boolean start(Player player) {
		return start(player, Config.getDefaultTime());
	}

	public void startGame() {
		releasePlayers();
		fillChests();
		for (String p : stats.keySet()) {
			Player player = Bukkit.getServer().getPlayer(p);
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
			Player player = Bukkit.getServer().getPlayer(p);
			Plugin.unfreezePlayer(player);
		}

	}

	public void fillChests() {
		for (int cntr = 0; cntr < chests.size(); cntr++) {
			Location loc = chests.get(cntr);
			if(!(loc.getBlock().getState() instanceof Chest)){
			    chests.remove(loc);
			    cntr--;
			    continue;
			}
			Chest chest = (Chest) loc.getBlock().getState();
			Plugin.fillChest(chest);
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
			Plugin.error(player,
					String.format("You are not in the game %s.", name));
			return false;
		}
		Random rand = Plugin.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		spawnsTaken.remove(player.getName());
		while (spawnTaken(loc)) {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		}
		spawnsTaken.put(player.getName(), loc);
		stats.put(player.getName(), new PlayerStat());
		player.teleport(loc);
		if (!isRunning) {
			Plugin.freezePlayer(player);
		}
		return true;
	}

	public synchronized boolean join(Player player) {
		if (stats.containsKey(player.getName())) {
			Plugin.error(player, "You are already in this game.");
			return false;
		}
		if (!Plugin.hasInventoryBeenCleared(player)) {
			Plugin.error(
					player,
					"You must clear your inventory first (Be sure to check you're not wearing armor either).");
			return false;
		}
		if (spawnsTaken.size() >= spawnPoints.size()) {
			Plugin.error(player, String.format("%s is already full.", name));
			return false;
		}
		if (!enabled) {
			Plugin.error(player,
					String.format("%s is currently not enabled.", name));
			return false;
		}
		Random rand = Plugin.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		while (spawnTaken(loc)) {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		}
		spawnsTaken.put(player.getName(), loc);
		stats.put(player.getName(), new PlayerStat());
		Plugin.giveMultiversePermission(player);
		player.teleport(loc);
		if (!isRunning) {
			Plugin.freezePlayer(player);
		}
		return true;
	}

	private boolean spawnTaken(Location loc) {

		for (Location comp : spawnsTaken.values()) {
			if (Plugin.equals(loc, comp)) {
				return true;
			}

		}
		return false;
	}

	public synchronized boolean leave(Player player) {
		if (!stats.containsKey(player.getName())) {
			Plugin.error(player,
					String.format("You are not in the game %s.", name));
			return false;
		}
		stats.remove(player.getName());
		readyToPlay.remove(player.getName());
		spawnsTaken.remove(player.getName());
		Plugin.unfreezePlayer(player);
		Plugin.takeMultiversePermission(player);
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
			Plugin.send(player,
					String.format("Teleporting you to %s's spawn.", name));
		} else {
			Plugin.error(
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
				Plugin.broadcast("Strangely, there was no winner left.");
			} else {
				Plugin.broadcast(String.format(
						"%s has won the game %s! Congratulations!",
						winner.getName(), name));
				spawnsTaken.remove(winner);
				teleportPlayerToSpawn(winner);
				Plugin.takeMultiversePermission(winner);
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
			Plugin.broadcastRaw(mess, ChatColor.WHITE);
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
		if (Plugin.isPlayerFrozen(killed)) {
			Plugin.unfreezePlayer(killed);
		}
		spawnsTaken.remove(killed.getName());
		Plugin.takeMultiversePermission(killed);
		checkForGameOver();
	}

	public void killed(Player killed) {
		if (!isRunning || stats.get(killed).isDead()) {
			return;
		}
		PlayerStat killedStat = getPlayerStat(killed.getName());
		killedStat.setDead(true);
		if (Plugin.isPlayerFrozen(killed)) {
			Plugin.unfreezePlayer(killed);
		}
		spawnsTaken.remove(killed.getName());
		Plugin.takeMultiversePermission(killed);
		checkForGameOver();
	}

	private Player getSurvivor() {
		for (String player : stats.keySet()) {
			PlayerStat stat = stats.get(player);
			if (!stat.isDead()) {
				return Bukkit.getServer().getPlayer(player);
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
		Plugin.send(player, String.format(
				"<name>[kills], %sGreen: alive, %sRed: dead",
				ChatColor.GREEN.toString(), ChatColor.RED.toString()));
		Plugin.send(player, "");
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
			Plugin.send(player, mess);
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
			if (Plugin.equals(l, loc)) {
				return false;
			}

		}
		chests.add(loc);
		return true;
	}

	public boolean addSpawnPoint(Location loc) {
		for (Location l : spawnPoints) {
			if (Plugin.equals(l, loc)) {
				return false;
			}

		}
		spawnPoints.add(loc);
		return true;
	}

	public boolean removeChest(Location loc) {
		Iterator<Location> iterator = chests.iterator();
		while (iterator.hasNext()) {
			if (Plugin.equals(loc, iterator.next())) {
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
			if (Plugin.equals(loc, l = iterator.next())) {
				iterator.remove();
				for (String p : spawnsTaken.keySet()) {
					Location comp = spawnsTaken.get(p);
					if (Plugin.equals(l, comp)) {
						spawnsTaken.remove(p);
						Player player = Bukkit.getServer().getPlayer(p);
						if (player != null) {
							Plugin.error(
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
