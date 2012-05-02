package com.randude14.hungergames.games;

import com.randude14.hungergames.Config;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import com.randude14.hungergames.GameCountdown;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Plugin;
import com.randude14.hungergames.api.event.*;

import org.bukkit.inventory.ItemStack;

public class HungerGame implements Comparable<HungerGame> {
	private final Map<Player, PlayerStat> stats;
	private final Map<Player, Location> spawnsTaken;
	private final Map<Player, InventorySave> savedInventories;
	private final List<Location> spawnPoints;
	private final List<Location> chests;
	private final List<Player> readyToPlay;
	private final String name;
	private Location spawn;
	private boolean isRunning;
	private boolean isCounting;
	private boolean enabled;
	private String setup;
	private List<String> itemsets;

	public HungerGame(String name) {
		this.name = name;
		spawnPoints = new ArrayList<Location>();
		chests = new ArrayList<Location>();
		readyToPlay = new ArrayList<Player>();
		spawnsTaken = new HashMap<Player, Location>();
		savedInventories = new HashMap<Player, InventorySave>();
		stats = new TreeMap<Player, PlayerStat>(new PlayerComparator());
		spawn = null;
		isRunning = isCounting = false;
		setup = null;
		itemsets = new ArrayList<String>();
		enabled = true;
	}

	public HungerGame(String name, String setup) {
		this(name);
		this.setup = setup;
	}

	public void loadFrom(ConfigurationSection section) {
		if (section.contains("spawn-points")) {
			ConfigurationSection spawnPointsSection = section
					.getConfigurationSection("spawn-points");
			for (String key : spawnPointsSection.getKeys(false)) {
				String str = spawnPointsSection.getString(key);
				Location loc = Plugin.parseToLoc(str);
				if (loc == null) {
					Plugin.warning("failed to load location '%s'", str);
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
					Plugin.warning("failed to load location '%s'", str);
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Plugin.warning("'%s' is no longer a chest.", str);
					continue;
				}
				chests.add(loc);
			}

		}
		enabled = section.getBoolean("enabled", Boolean.TRUE);
		spawn = Plugin.parseToLoc(section.getString("spawn"));
		Plugin.callEvent(new GameLoadEvent(this));
	}

	public void saveTo(ConfigurationSection section) {
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
		if (getSpawn() != null) {
			section.set("spawn", Plugin.parseToString(getSpawn()));
		}
		Plugin.callEvent(new GameSaveEvent(this));
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
		if (readyToPlay.contains(player)) {
			Plugin.error(player,
					"You have already cast your vote that you are ready to play.");
			return false;
		}
		if (isCounting) {
			Plugin.error(player, "%s is already counting down.", name);
			return false;
		}
		if (isRunning) {
			Plugin.error(player, "%s is already running a game.", name);
			return false;
		}
		readyToPlay.add(player);
		int minVote = Config.getMinVote(setup);
		if (readyToPlay.size() >= minVote/* && stats.size() == readyToPlay.size() */) {
			// TODO add config option to require all who have join to be ready
			Plugin.broadcast(String
					.format(
					// "Everyone is ready to play %s. Starting game...",
					"Enough players have voted that they are ready. Starting game...",
							this.name));
			start(player);
		} else {
			String mess = Config.getVoteMessage(setup)
					.replace("<player>", player.getName())
					.replace("<game>", this.name);
			Plugin.broadcast(mess);
		}
		return true;
	}

	public boolean start(Player player, int ticks) {// TODO stop
		if (isRunning) {
			return false;
		}

		if (stats.size() < Config.getMinPlayers(setup)) {
			Plugin.error(player, "There are not enough players in %s", name);
			return false;
		}
		if (isCounting) {
			Plugin.error(player, "%s is already counting down.", name);
			return false;
		}
		if (!enabled) {
			Plugin.error(player, "%s is currently not enabled.", name);
			return false;
		}
		GameStartEvent event = new GameStartEvent(this);
		Plugin.callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		if (ticks <= 0) {
			Plugin.broadcast("Starting %s. Go!!", name);
			startGame();
		} else {
			new GameCountdown(this, ticks);
			isCounting = true;
		}
		return true;
	}

	public boolean start(Player player) {
		return start(player, Config.getDefaultTime(setup));
	}

	public void startGame() {
		releasePlayers();
		fillChests();
		for (Player p : stats.keySet()) {
			if (p == null) {
				continue;
			}
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		isRunning = true;
		isCounting = false;
		readyToPlay.removeAll(readyToPlay);
	}

	// TODO stopGame(), call GameStopEvent()

	public void releasePlayers() {
		for (Player p : stats.keySet()) {
			Plugin.unfreezePlayer(p);
		}

	}

	public void fillChests() {
		for (int cntr = 0; cntr < chests.size(); cntr++) {
			Location loc = chests.get(cntr);
			if (!(loc.getBlock().getState() instanceof Chest)) {
				continue;
			}
			Chest chest = (Chest) loc.getBlock().getState();
			Plugin.fillChest(chest, itemsets);
		}

	}

	public void setEnabled(boolean flag) {
		enabled = flag;
	}

	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}

	public synchronized boolean rejoin(Player player) {
		if (!Config.getAllowRejoin(setup)) {
			Plugin.error(player, "You are not allowed to rejoin a game.");
			return false;
		}
		if (!stats.containsKey(player) || stats.get(player).hasRunOutOfLives()) {
			Plugin.error(player, "You are not in the game %s.", name);
			return false;
		}
		PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player, true);
		Plugin.callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		spawnsTaken.remove(player);
		return addPlayer(player);
	}

	public synchronized boolean join(Player player) {
		if (stats.containsKey(player)) {
			Plugin.error(player, "You are already in this game.");
			return false;
		}
		if (isRunning && !Config.getAllowJoinWhileRunning(setup)) {
			Plugin.error(
					player,
					"%s is already running and you cannot join while that is so.",
					name);
			return false;
		}
		PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
		Plugin.callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		if (!addPlayer(player))
			return false;

		stats.put(player, new PlayerStat(player));
		return true;
	}

	public synchronized boolean addPlayer(Player player) {
		if (!enabled) {
			Plugin.error(player, "%s is currently not enabled.", name);
			return false;
		}

		if (spawnsTaken.size() >= spawnPoints.size()) {
			Plugin.error(player, "%s is already full.", name);
			return false;
		}

		Random rand = Plugin.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		while (spawnTaken(loc)) {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		}
		spawnsTaken.put(player, loc);
		savedInventories.put(player, new InventorySave(player));
		player.getInventory().clear();
		player.teleport(loc);
		if (!isRunning) {
			Plugin.freezePlayer(player);
		}
		return true;
	}

	private boolean spawnTaken(Location loc) {
		if (spawnsTaken.containsValue(loc))
			return true;

		return false;
	}

	public synchronized boolean leave(Player player) {
		if (!stats.containsKey(player)) {
			Plugin.error(player, "You are not in the game %s.", name);
			return false;
		}
		playerLeaving(player);
		dropInventory(player);
		loadSavedInventory(player);
		if (isRunning && !Config.getAllowRejoin(setup)) {
			stats.get(player).death();
		}
		teleportPlayerToSpawn(player);
		if (isRunning) {
			checkForGameOver(false);
		}
		Plugin.callEvent(new PlayerLeaveGameEvent(this, player));
		return true;
	}

	private synchronized void playerLeaving(Player player) {
		spawnsTaken.remove(player);
		Plugin.unfreezePlayer(player);
		savedInventories.remove(player).loadInventoryTo(player);
		if (spawn != null)
			GameManager.addPlayerRespawn(player, spawn);
	}

	private void loadSavedInventory(Player player) {
		if (!savedInventories.containsKey(player))
			return;
		savedInventories.remove(player).loadInventoryTo(player);
	}

	private void clear() {
		stats.clear();
		spawnsTaken.clear();
		readyToPlay.clear();
		isRunning = false;
		isCounting = false;
	}

	public void teleportPlayerToSpawn(Player player) {
		if (player == null) {
			return;
		}
		if (getSpawn() != null) {
			player.teleport(getSpawn());
			Plugin.send(player, "Teleporting you to %s's spawn.", name);
		} else {
			Plugin.error(
					player,
					"There was no spawn set for %s. Please contact an admin for help.",
					name);
			player.setHealth(0); // Should wake up at spawn
		}

	}

	public void checkForGameOver(boolean notifyOfRemaining) {// TODO config
																// option
		if (isOver()) {
			Player winner = getSurvivor();
			GameEndEvent event;
			if (winner == null) {
				Plugin.broadcast("Strangely, there was no winner left.");
				event = new GameEndEvent(this);
			} else {
				Plugin.broadcast("%s has won the game %s! Congratulations!",
						winner.getName(), name);
				playerLeaving(winner);
				if (!Config.getWinnerKeepsItems(setup)) {
					dropInventory(winner);
				}
				teleportPlayerToSpawn(winner);
				loadSavedInventory(winner);
				event = new GameEndEvent(this, winner);
			}
			isRunning = false;
			clear();
			Plugin.callEvent(event);
		}

		if (!notifyOfRemaining)
			return;

		List<Player> remaining = new ArrayList<Player>();
		for (Player player : stats.keySet()) {
			if (!stats.get(player).hasRunOutOfLives()) {
				remaining.add(player);
			}

		}

		String mess = "Remaining players: ";
		for (int cntr = 0; cntr < remaining.size(); cntr++) {
			mess += remaining.get(cntr).getName();
			if (cntr < remaining.size() - 1) {
				mess += ", ";
			}

		}
		Plugin.broadcastRaw(mess, ChatColor.WHITE);
	}

	public String getInfo() {
		return String.format("%s[%d/%d] Enabled: %b", name, stats.size(),
				spawnPoints.size(), enabled);
	}

	public boolean contains(Player... players) {
		for (Player player : players) {
			if (!stats.containsKey(player)
					|| stats.get(player).hasRunOutOfLives()) {
				return false;
			}

		}
		return true;
	}

	public void killed(Player killer, Player killed) {
		if (!isRunning || stats.get(killed).hasRunOutOfLives())
			return;

		PlayerStat killerStat = getPlayerStat(killer);
		killerStat.kill();
		String message = Config.getKillMessage(setup)
				.replace("<killer>", killer.getName())
				.replace("<killed>", killed.getName())
				.replace("<game>", name);
		killed(killed, false);
		PlayerKillEvent event = new PlayerKillEvent(this, killer, killed,
				message);
		Plugin.callEvent(event);
		Plugin.broadcast(message);
	}

	public void killed(Player killed) {
		killed(killed, true);
	}

	private void killed(Player killed, boolean callEvent) {
		if (!isRunning)
			return;

		PlayerStat killedStat = getPlayerStat(killed);
		killedStat.death();
		if (killedStat.hasRunOutOfLives()) {
			playerLeaving(killed);
			loadSavedInventory(killed);
		}

		else {
			if (Config.shouldRespawnAtSpawnPoint(setup)) {
				Random rand = Plugin.getRandom();
				Location respawn = spawnPoints.get(rand.nextInt(spawnPoints
						.size()));
				GameManager.addPlayerRespawn(killed, respawn);
			}
			Plugin.info("You have " + killedStat.getLivesLeft()
					+ " lives left.");
		}
		if (callEvent) {
			Plugin.callEvent(new PlayerKillEvent(this, killed));
		}

	}

	private Player getSurvivor() {
		for (Player player : stats.keySet()) {
			PlayerStat stat = stats.get(player);
			if (!stat.hasRunOutOfLives()) {
				return player;
			}

		}
		return null;
	}

	public boolean isOver() {
		int numAlive = 0;
		for (Player player : stats.keySet()) {
			PlayerStat stat = stats.get(player);
			if (!stat.hasRunOutOfLives()) {
				numAlive++;
			}

		}
		return numAlive < 2;
	}

	public PlayerStat getPlayerStat(Player player) {
		return stats.get(player);
	}

	public void listStats(Player player) {
		Plugin.send(player, "<name>[lives/kills]", ChatColor.GREEN.toString(),
				ChatColor.RED.toString());
		Plugin.send(player, "");
		List<Player> players = new ArrayList<Player>(stats.keySet());
		for (int cntr = 0; cntr < stats.size(); cntr += 5) {
			String mess = "";
			for (int i = cntr; i < cntr + 5 && i < stats.size(); i++) {
				Player p = players.get(i);
				PlayerStat stat = stats.get(p);
				mess += String.format("%s[%d/%d]", p, stat.getDeaths(),
						stat.getKills()); // TODO stat.getDeaths() - maxLives
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
				for (Player p : spawnsTaken.keySet()) {
					Location comp = spawnsTaken.get(p);
					if (Plugin.equals(l, comp)) {
						spawnsTaken.remove(p);
						if (p != null) {
							Plugin.error(
									p,
									"Your spawn point has been recently removed. Try rejoining by typing '/hg join %s'",
									name);
							leave(p);
						}

					}

				}
				return true;
			}

		}
		return false;
	}

	private static void dropInventory(Player player) {
		for (ItemStack i : player.getInventory().getContents()) {
			if (i == null || i.getType().equals(Material.AIR))
				continue;
			player.getWorld().dropItemNaturally(player.getLocation(), i);
		}
		player.getInventory().clear();
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setCounting(boolean counting) {
		isCounting = counting;
	}

	public String getSetup() {
		return (setup == null || "".equals(setup)) ? null : setup;
	}

	public List<String> getItemSets() {
		return itemsets;
	}

	public void addItemSet(String name) {
		itemsets.add(name);
	}

	public void removeItemSet(String name) {
		itemsets.remove(name);
	}

	// sorts players by name ignoring case
	private class PlayerComparator implements Comparator<Player> {

		public PlayerComparator() {
		}

		public int compare(Player p1, Player p2) {
			String name1 = p1.getName();
			String name2 = p2.getName();
			return name1.compareToIgnoreCase(name2);
		}

	}

}
