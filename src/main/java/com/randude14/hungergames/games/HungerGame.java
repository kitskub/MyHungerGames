package com.randude14.hungergames.games;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameCountdown;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.reset.ResetHandler;
import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.utils.ChatUtils;
	
public class HungerGame implements Comparable<HungerGame> {
	private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final Map<String, Location> spawnsSaved;
	private final Map<String, Location> spectators;
	private final Map<String, GameMode> spectatorGameMode;
	private final List<Location> spawnPoints;
	private final List<Location> chests;
	private final List<String> readyToPlay;
	private final List<String> allPlayers; // This might be used for rollback stuff, later
	private final String name;
	private GameCountdown countdown;
	private Location spawn;
	private List<String> itemsets;
	private String setup;
	private boolean isRunning;
	private boolean isCounting;
	private boolean isPaused;
	private boolean enabled;
        private List<InventoryHolder> randomInvs;


	public HungerGame(String name) {
		this.name = name;
		spawnPoints = new ArrayList<Location>();
		chests = new ArrayList<Location>();
		readyToPlay = new ArrayList<String>();
		spawnsTaken = new HashMap<String, Location>();
		spawnsSaved = new HashMap<String, Location>();
		spectators = new HashMap<String, Location>();
		spectatorGameMode = new HashMap<String, GameMode>();
		stats = new TreeMap<String, PlayerStat>();
		countdown = null;
		allPlayers = new ArrayList<String>();
		spawn = null;
		isRunning = isCounting = isPaused = false;
		setup = null;
		itemsets = new ArrayList<String>();
		enabled = true;
                randomInvs = new ArrayList<InventoryHolder>();
	}

	public HungerGame(String name, String setup) {
		this(name);
		this.setup = setup;
	}

	public void loadFrom(ConfigurationSection section) {
		if (section.contains("spawn-points")) {
			ConfigurationSection spawnPointsSection = section.getConfigurationSection("spawn-points");
			for (String key : spawnPointsSection.getKeys(false)) {
				String str = spawnPointsSection.getString(key);
				Location loc = null;
				try {
					loc = HungerGames.parseToLoc(str);
				}
				catch (NumberFormatException e) {}
				if (loc == null) {
					Logging.warning("failed to load location '%s'", str);
					continue;
				}
				spawnPoints.add(loc);
			}

		}

		if (section.contains("chests")) {
			ConfigurationSection chestsSection = section.getConfigurationSection("chests");
			for (String key : chestsSection.getKeys(false)) {
				String str = chestsSection.getString(key);
								Location loc = null;
				try {
					loc = HungerGames.parseToLoc(str);
				}
				catch (NumberFormatException e) {}
				if (loc == null) {
					Logging.warning("failed to load location '%s'", str);
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Logging.warning("'%s' is no longer a chest.", str);
					continue;
				}
				chests.add(loc);
			}

		}
                
                if(section.isList("itemsets")) {
                    itemsets = section.getStringList("itemsets");
                }
                
		enabled = section.getBoolean("enabled", Boolean.TRUE);
		try {
			spawn = HungerGames.parseToLoc(section.getString("spawn"));
		} 
		catch (NumberFormatException numberFormatException) {}
		HungerGames.callEvent(new GameLoadEvent(this));
	}

	public void saveTo(ConfigurationSection section) {
		if (!spawnPoints.isEmpty()) {
			ConfigurationSection spawnPointsSection = section.createSection("spawn-points");
			for (int cntr = 0; cntr < spawnPoints.size(); cntr++) {
				Location loc = spawnPoints.get(cntr);
				if (loc == null) continue;
				spawnPointsSection.set("spawnpoint" + (cntr + 1), HungerGames.parseToString(loc));
			}
		}

		if (!chests.isEmpty()) {
			ConfigurationSection chestsSection = section.createSection("chests");
			for (int cntr = 0; cntr < chests.size(); cntr++) {
				Location loc = chests.get(cntr);
				chestsSection.set("chest" + (cntr + 1), HungerGames.parseToString(loc));
			}

		}
                
                if(!itemsets.isEmpty()) {
                    section.set("itemsets", itemsets);
                }
                
		section.set("enabled", enabled);
		if (getSpawn() != null) section.set("spawn", HungerGames.parseToString(getSpawn()));
		
		HungerGames.callEvent(new GameSaveEvent(this));
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
		if (readyToPlay.contains(player.getName())) {
			ChatUtils.error(player, "You have already cast your vote that you are ready to play.");
			return false;
		}
		if (isCounting) {
			ChatUtils.error(player, "%s is already counting down.", name);
			return false;
		}
		if (isRunning) {
			ChatUtils.error(player, "%s is already running a game.", name);
			return false;
		}
		if(isPaused) {
			ChatUtils.error(player, "%s has been paused.", name);
			return false;
		}
		readyToPlay.add(player.getName());
		int minVote = Config.getMinVote(setup);
		if ((readyToPlay.size() >= minVote && stats.size() >= Config.getMinPlayers(setup))
		    || (readyToPlay.size() >= stats.size() && Config.getAllVote(setup) && !Config.getAutoVote(setup))) {
			ChatUtils.broadcast("Enough players have voted that they are ready. Starting game...", this.name);
			startGame(player, false);
		} else {
			String mess = Config.getVoteMessage(setup)
					.replace("<player>", player.getName())
					.replace("<game>", this.name);
			ChatUtils.broadcast(mess);
		}
		return true;
	}

	public void addSpectator(Player player) {
		spectators.put(player.getName(), player.getLocation());
		Random rand = HungerGames.getRandom();
		Location loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
		player.teleport(loc);
		spectatorGameMode.put(player.getName(), player.getGameMode());
		player.setGameMode(GameMode.CREATIVE);
	}

	public boolean isSpectating(Player player) {
		return spectators.containsKey(player.getName());
	}

	public void removeSpectator(Player player) {
		player.teleport(spectators.remove(player.getName()));
		player.setGameMode(spectatorGameMode.get(player.getName()));
	}

	public void getSpectatorLocation(Player player) {
		spectators.get(player.getName());
	}
	
	public boolean stopGame(Player player, boolean isFinished) {
		String result = stopGame(isFinished);
		if (result != null) {
			ChatUtils.error(player, result);
			return false;
		}
		return true;
	}
	
	public String stopGame(boolean isFinished) {
		if (!isRunning && !isPaused && !isCounting) return "Game is not started";

		if (!enabled) {
			return "%s is currently not enabled.";
		}
		if (!isFinished) {
			GameStopEvent event = new GameStopEvent(this);
			HungerGames.callEvent(event);
		}
		for (Player player : getRemainingPlayers()) {
			teleportPlayerToSpawn(player);
			GameManager.unfreezePlayer(player);
			ItemStack[] contents = player.getInventory().getContents();
			player.getInventory().clear();
			InventorySave.loadInventory(player);
			if (isFinished && Config.getWinnerKeepsItems(setup)) player.getInventory().addItem(contents);
		}
		clear();
		ResetHandler.resetChanges(this);
		return null;
	}
	
	/**
	 * Starts the game with the specified number of ticks
	 * 
	 * @param player
	 * @param ticks
	 * @return true if game or countdown was successfully started
	 */
	public boolean startGame(Player player, int ticks) {
		if (ticks <= 0) {
			String result = startGame(0);
			if (result != null) {
				ChatUtils.error(player, result);
				return false;
			}
		} else {
			countdown = new GameCountdown(this, ticks, player);
			isCounting = true;
		}
		return true;
	}

	/**
	 * Starts this game with the default time if immediate is true. Otherwise, starts the game immediately.
	 * 
	 * @param player starter
	 * @param immediate
	 * @return
	 */
	public boolean startGame(Player player, boolean immediate) {
		if(!immediate) return startGame(player, Config.getDefaultTime(setup));
		return startGame(player, 0);
	}

	/**
	 * Starts the game
	 * 
	 * @param ticks 
	 * @return Null if game or countdown was not successfully started. Otherwise, error message.
	 */
	public String startGame(int ticks) {
		if (ticks > 0) {
			countdown = new GameCountdown(this, ticks);
			isCounting = true;
			return null;
		}
				
		if (isRunning) return "Game is already running";

		if (stats.size() < Config.getMinPlayers(setup)) return "There are not enough players in %s";
		if (isCounting) return "%s is already counting down.";
		if (!enabled) return "%s is currently not enabled.";
		
		GameStartEvent event = new GameStartEvent(this);
		HungerGames.callEvent(event);
		if (event.isCancelled()) {
			return "Start was cancelled.";
		}
		
		releasePlayers();
		fillInventories();
		for (String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		isRunning = true;
		isCounting = false;
		isPaused = false;
		readyToPlay.clear();
		countdown = null;
		ChatUtils.broadcast("Starting %s. Go!!", name);
		return null;
	}
	
 	public boolean resumeGame(Player player, int ticks) {		
		if (ticks <= 0) {
			String result = resumeGame(0);
			if (result != null) {
				ChatUtils.error(player, result);
				return false;
			}
		} else {
			countdown = new GameCountdown(this, ticks, true);
			isCounting = true;
		}
		return true;
	}
	
	public boolean resumeGame(Player player, boolean immediate) {
		if (!immediate) return resumeGame(player, Config.getDefaultTime(setup));
		return resumeGame(player, 0);
	}
	
	/**
	 * Resumes the game
	 * 
	 * @param ticks 
	 * @return Null if game or countdown was not successfully started. Otherwise, error message.
	 */
	public String resumeGame(int ticks) {
		if(!isPaused) return "Cannot resume a game that has not been paused.";
		if (ticks > 0) {
			countdown = new GameCountdown(this, ticks, true);
			isCounting = true;
			return null;
		}
		GameStartEvent event = new GameStartEvent(this, true);
		HungerGames.callEvent(event);
		if (event.isCancelled()) {
			return "Start was cancelled.";
		}
		
		isRunning = true;
		isPaused = false;
		isCounting = false;
		countdown = null;
		for(String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			InventorySave.saveAndClearInventory(p);
			InventorySave.loadGameInventory(p);
			p.teleport(spawnsSaved.remove(playerName));
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		ChatUtils.broadcast("Resuming %s. Go!!", name);
		return null;
	}
	
	public boolean pauseGame(Player player) {
		String result = pauseGame();
		if (result != null) {
			ChatUtils.error(player, "Cannot pause a game that has been paused.");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return null if successful, message if not
	 */
	public String pauseGame() {
		if(isPaused) return "Cannot pause a game that has been paused.";
		
		HungerGames.callEvent(new GamePauseEvent(this));
		isRunning = false;
		isCounting = false;
		isPaused = true;
		if(countdown != null) {
			countdown.cancel();
			countdown = null;
		}
		for(String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			InventorySave.saveAndClearGameInventory(p);
			InventorySave.loadInventory(p);
			spawnsSaved.put(playerName, p.getLocation());
			teleportPlayerToSpawn(p);
		}
		return null;
	}
	
	private void releasePlayers() {
		for (String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			GameManager.unfreezePlayer(p);
		}

	}

	public void addAndFillInventory(Inventory inv) {
		if(!randomInvs.contains(inv.getHolder())) {
			Logging.debug("Inventory Holder was not in randomInvs.");
			HungerGames.fillInventory(inv, itemsets);
			randomInvs.add(inv.getHolder());
		}
	}
        
	public void fillInventories() {
	    for (int cntr = 0; cntr < chests.size(); cntr++) {
		Location loc = chests.get(cntr);
		if (!(loc.getBlock().getState() instanceof Chest)) continue;
		Chest chest = (Chest) loc.getBlock().getState();
		HungerGames.fillInventory(chest.getInventory(), itemsets);
	    }

	}

	public void setEnabled(boolean flag) {
		enabled = flag;
	}

	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}

	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (String s : allPlayers) {
		    if (Bukkit.getPlayer(s) == null) continue;
		    players.add(Bukkit.getPlayer(s));
		}
		return players;
	}
	public synchronized boolean rejoin(Player player) {
	    if(!playerEnteringPreProcess(player)) return false;
	    if (!Config.getAllowRejoin(setup)) {
		    ChatUtils.error(player, "You are not allowed to rejoin a game.");
		    return false;
	    }
	    if (!stats.containsKey(player.getName()) || stats.get(player.getName()).hasRunOutOfLives()) {
		    ChatUtils.error(player, "You are not in the game %s.", name);
		    return false;
	    }
	    if (stats.get(player.getName()).isPlaying()){
		    ChatUtils.error(player, "You can't rejoin a game while you are in it.");
		    return false;
	    }
	    PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player, true);
	    HungerGames.callEvent(event);
	    if (event.isCancelled()) return false;
	    if (!playerEntering(player)) return false;
	    return true;
	}

	public synchronized boolean join(Player player) {
	    if(!playerEnteringPreProcess(player)) return false;
	    if (stats.containsKey(player.getName())) {
		    ChatUtils.error(player, "You are already in this game.");
		    return false;
	    }
	    if (isRunning && !Config.getAllowJoinWhileRunning(setup)) {
		    ChatUtils.error(player, "%s is already running and you cannot join while that is so.", name);
		    return false;
	    }
		if(isPaused) {
			ChatUtils.error(player, "%s has been paused.", name);
			return false;
		}
	    PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
	    HungerGames.callEvent(event);
	    if (event.isCancelled()) return false;
	    if(!playerEntering(player)) return false;
	    stats.put(player.getName(), new PlayerStat(player));
	    if (Config.getAutoVote(setup)) readyToPlay.add(player.getName());
	    return true;
	}
	
	private synchronized boolean playerEnteringPreProcess(Player player) {
	    if (!enabled) {
		    ChatUtils.error(player, "%s is currently not enabled.", name);
		    return false;
	    }

	    if (spawnsTaken.size() >= spawnPoints.size()) {
		    ChatUtils.error(player, "%s is already full.", name);
		    return false;
	    }

	    if (Config.getShouldClearInv(setup)) {
		    if(!HungerGames.hasInventoryBeenCleared(player)) {
			    ChatUtils.error(player, "You must clear your inventory first (Be sure you're not wearing armor either).");
			    return false;
		    }
	    }
	    return true;
	}
	
	public synchronized boolean playerEntering(Player player) {
	    Location loc = getNextOpenSpawnPoint();
	    spawnsTaken.put(player.getName(), loc);
	    player.teleport(loc);
	    if(!Config.getShouldClearInv(setup)) InventorySave.saveAndClearInventory(player);
	    if (!isRunning) GameManager.freezePlayer(player);
	    allPlayers.add(player.getName());
	    return true;
	}
	
	public Location getNextOpenSpawnPoint() {
		Random rand = HungerGames.getRandom();
		Location loc;
		do {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
			if (loc == null) spawnPoints.remove(loc);
			
		} while (spawnTaken(loc));
		return loc;
	}
	
	private boolean spawnTaken(Location loc) {
	    if (spawnsTaken.containsValue(loc) || loc == null) return true;
	    return false;
	}

	public synchronized boolean leave(Player player) {
	    if (!isPlaying(player)) {
		ChatUtils.error(player, "You are not playing the game %s.", name);
		return false;
	    }
	    if (!Config.getAllowRejoin(setup)) {
		stats.get(player.getName()).die();
	    }
	    else {
		stats.get(player.getName()).setPlaying(false);
	    }
	    dropInventory(player);
	    teleportPlayerToSpawn(player);
	    playerLeaving(player);
	    checkForGameOver(false);
	    HungerGames.callEvent(new PlayerLeaveGameEvent(this, player));
	    return true;
	}
	
	public synchronized boolean quit(Player player) {
	    if (!contains(player)) {
		ChatUtils.error(player, "You are not in the game %s.", name);
		return false;
	    }
	    if(isRunning) {
		stats.get(player.getName()).die();
	    }
	    else {
		stats.remove(player.getName());
	    }
	    if (isPlaying(player)) {
		    dropInventory(player);
		    teleportPlayerToSpawn(player);
	    }
	    playerLeaving(player);
	    checkForGameOver(false);
	    HungerGames.callEvent(new PlayerQuitGameEvent(this, player));
	    return true;
	}
	
	private synchronized void playerLeaving(Player player) {
		spawnsTaken.remove(player.getName());
		spawnsSaved.remove(player.getName());
		GameManager.unfreezePlayer(player);
		InventorySave.loadInventory(player);
	}

	private void clear() {
		stats.clear();
		spawnsTaken.clear();
		readyToPlay.clear();
		randomInvs.clear();
		allPlayers.clear();
		isRunning = false;
		isCounting = false;
		isPaused = false;
	}

	public void teleportPlayerToSpawn(Player player) {
		if (player == null) {
			return;
		}
		if (getSpawn() != null) {
			player.teleport(getSpawn());
			ChatUtils.send(player, "Teleporting you to %s's spawn.", name);
		} else {
			ChatUtils.error(player, "There was no spawn set for %s. Please contact an admin for help.", name);
			player.teleport(player.getWorld().getSpawnLocation());
		}

	}

	/**
	 * 
	 * @param notifyOfRemaining
	 * @return true if is over, false if not
	 */
	public boolean checkForGameOver(boolean notifyOfRemaining) {// TODO config option
	    if(!isRunning) return false;
	    List<Player> remaining = getRemainingPlayers();
	    if (remaining.size() < 2) {
		    Player winner = remaining.get(0);
		    GameEndEvent event;
		    if (winner == null) {
			    ChatUtils.broadcast("Strangely, there was no winner left.");
			    event = new GameEndEvent(this);
		    } else {
			    ChatUtils.broadcast("%s has won the game %s! Congratulations!", winner.getName(), name);
			    event = new GameEndEvent(this, winner);
		    }
		    HungerGames.callEvent(event);
		    stopGame(winner, true);
		    return true;
	    }

	    if (!notifyOfRemaining) return false;
	    String mess = "Remaining players: ";
	    for (int cntr = 0; cntr < remaining.size(); cntr++) {
		    mess += remaining.get(cntr).getName();
		    if (cntr < remaining.size() - 1) {
			    mess += ", ";
		    }

	    }
	    ChatUtils.broadcastRaw(mess, ChatColor.WHITE);
	    return false;
	}

	public String getInfo() {
		return String.format("%s[%d/%d] Enabled: %b", name, stats.size(), spawnPoints.size(), enabled);
	}

	/**
	 * 
	 * @param players players to check
	 * @return true if players are in the game and have lives, regardless if they are playing or not
	 */
	public boolean contains(Player... players) {
	    for (Player player : players) {
		if (!stats.containsKey(player.getName()) || stats.get(player.getName()).hasRunOutOfLives()) {
		    return false;
		}
	    }
	    return true;
	}
	
	/**
	 * 
	 * @param players players to check
	 * @return true if players are in the game and have lifes, regardless if they are playing or not
	 */
	public boolean isPlaying(Player... players) {
	    for (Player player : players) {
		if (!stats.containsKey(player.getName()) || stats.get(player.getName()).hasRunOutOfLives() || !stats.get(player.getName()).isPlaying()) {
		    return false;
		}
	    }
	    return true;
	}

	public void killed(Player killer, Player killed) {
		if (!isRunning || stats.get(killed.getName()).hasRunOutOfLives()) return;

		PlayerStat killerStat = getPlayerStat(killer);
		killerStat.kill();
		String message = Config.getKillMessage(setup)
				.replace("<killer>", killer.getName())
				.replace("<killed>", killed.getName())
				.replace("<game>", name);
		killed(killed, false);
		PlayerKillEvent event = new PlayerKillEvent(this, killer, killed, message);
		HungerGames.callEvent(event);
		ChatUtils.broadcast(message);
	}

	public void killed(Player killed) {
		killed(killed, true);
	}

	private void killed(Player killed, boolean callEvent) {
		if (!isRunning) return;

		PlayerStat killedStat = getPlayerStat(killed);
		killedStat.death();
		if (killedStat.hasRunOutOfLives()) {
			playerLeaving(killed);
			InventorySave.loadInventory(killed);
		}

		else {
			if (Config.shouldRespawnAtSpawnPoint(setup)) {
				Location respawn = spawnsTaken.get(killed.getName());
				GameManager.addPlayerRespawn(killed, respawn);
			}
			else {
				Location respawn = spawnsTaken.get(killed.getName());
				GameManager.addPlayerRespawn(killed, respawn);
				// TODO needs a random
			}
			ChatUtils.send(killed, "You have " + killedStat.getLivesLeft() + " lives left.");
		}
		checkForGameOver(false);
		if (callEvent) {
			HungerGames.callEvent(new PlayerKillEvent(this, killed));
		}

	}
	
	/**
	 * 
	 * @return the remaining players that have lives and are playing
	 */
	public List<Player> getRemainingPlayers(){
	    List<Player> remaining = new ArrayList<Player>();
	    for (String playerName : stats.keySet()) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) continue;
		PlayerStat stat = stats.get(playerName);
		if (!stat.hasRunOutOfLives() && stat.isPlaying()) {
		    remaining.add(player);
		}
	    }
	    return remaining;
	}

	public PlayerStat getPlayerStat(Player player) {
		return stats.get(player.getName());
	}
	
	/* Will leave this here in case the new method sucks
	public void listStats(Player player) {
		ChatUtils.send(player, "<name>[lives/kills]", ChatColor.GREEN.toString(), ChatColor.RED.toString());
		ChatUtils.send(player, "");
		List<String> players = new ArrayList<String>(stats.keySet());
		for (int cntr = 0; cntr < stats.size(); cntr += 5) {
			String mess = "";
			for (int i = cntr; i < cntr + 5 && i < stats.size(); i++) {
				Player p = Bukkit.getPlayer(players.get(i));
				if (p == null) continue;
				PlayerStat stat = stats.get(players.get(i));
				mess += String.format("%s [%d/%d]", p.getName(), stat.getLivesLeft(), stat.getKills());
				if (i < cntr + 4 && cntr < stats.size() - 1) {
					mess += ", ";
				}

			}
			ChatUtils.send(player, mess);
		}

	}
	*/
	
	public void listStats(Player player) {
		int living = 0, dead = 0;
		List<String> players = new ArrayList<String>(stats.keySet());
		String mess = "";
		for (int cntr = 0; cntr < players.size(); cntr++) {
			PlayerStat stat = stats.get(players.get(cntr));
			Player p = stat.getPlayer();
			if (p == null) continue;
			String statName = "";
			if (stat.hasRunOutOfLives() || !stat.isPlaying()) {
				statName = ChatColor.RED.toString() + p.getName() + ChatColor.GRAY.toString();
				dead++;
			}
			else if (!stat.isPlaying()) {
				statName = ChatColor.YELLOW.toString() + p.getName() + ChatColor.GRAY.toString();
				dead++;
			}
			else {
				statName = ChatColor.GREEN.toString() + p.getName() + ChatColor.GRAY.toString();
				living++;
			}
			mess += String.format("%s [%d/%d]", statName, stat.getLivesLeft(), stat.getKills());
			if (players.size() >= cntr + 1) {
				mess += ", ";
			}
		}
		ChatUtils.send(player, "<name>[lives/kills]");
		ChatUtils.send(player, "Total Players: %s Total Living: %s Total Dead or Not Playing: %s", stats.size(), living, dead);
		ChatUtils.send(player, "");
		ChatUtils.send(player, mess);
	}

	public String getName() {
		return name;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean addChest(Location loc) {
		for (Location l : chests) {
		    if (HungerGames.equals(l, loc)) return false;
		}
		chests.add(loc);
		return true;
	}

	public boolean addSpawnPoint(Location loc) {
		if (loc == null) return true;
		for (Location l : spawnPoints) {
			if (HungerGames.equals(l, loc)) return false;
		}
		spawnPoints.add(loc);
		return true;
	}

	public boolean removeChest(Location loc) {
		Iterator<Location> iterator = chests.iterator();
		while (iterator.hasNext()) {
			if (HungerGames.equals(loc, iterator.next())) {
				iterator.remove();
				return true;
			}

		}
		return false;
	}

	public boolean removeSpawnPoint(Location loc) {
		if (loc == null) return true;
		Iterator<Location> iterator = spawnPoints.iterator();
		Location l = null;
		while (iterator.hasNext()) {
			if (HungerGames.equals(loc, l = iterator.next())) {
				iterator.remove();
				for (String playerName : spawnsTaken.keySet()) {
					Location comp = spawnsTaken.get(playerName);
					if (HungerGames.equals(l, comp)) {
						spawnsTaken.remove(playerName);
						if (Bukkit.getPlayer(playerName) == null) continue;
						ChatUtils.error(Bukkit.getPlayer(playerName),
							"Your spawn point has been recently removed. Try rejoining by typing '/hg join %s'", 
							playerName);
						leave(Bukkit.getPlayer(playerName));
					}
				}
				return true;
			}
		}
		return false;
	}

	private static void dropInventory(Player player) {
		for (ItemStack i : player.getInventory().getContents()) {
			if (i == null || i.getType().equals(Material.AIR)) continue;
			player.getWorld().dropItemNaturally(player.getLocation(), i);
		}
		player.getInventory().clear();
	}

	public Location getSpawn() {
		return spawn;
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
