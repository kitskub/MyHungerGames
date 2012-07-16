package com.randude14.hungergames.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Set;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameCountdown;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.reset.ResetHandler;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.stats.StatHandler;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.Cuboid;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

	
public class HungerGame implements Comparable<HungerGame>, Runnable{
	// Per game
	private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final Set<String> allPlayers;
	private final List<Location> randomLocs;
	private final Map<String, String> sponsors; // Just a list for info, <sponsor, sponsee>
	private final SpectatorSponsoringRunnable spectatorSponsoringRunnable;
	private boolean isRunning;
	private boolean isCounting;
	private boolean isPaused;

	// Persistent
	private final List<Location> chests;
	private final Map<Location, String> fixedChests;
	private final List<Location> spawnPoints;
	private final String name;
	private String setup;
	private final List<String> itemsets;
	private final Set<String> worlds;
	private final Set<Cuboid> cuboids;
	private boolean enabled;
	private Location spawn;

	
	// Temporary
	private final Map<String, Location> playerLocs;// For pausing
	private final Map<String, Location> spectators;
	private final Map<String, Boolean> spectatorFlying; // If a spectator was flying
	private final Map<String, Boolean> spectatorFlightAllowed; // If a spectator's flight was allowed
	private final Map<String, GameMode> playerGameModes; // Whether a player was in survival when game started
	private final List<String> readyToPlay;
	private GameCountdown countdown;
	private int locTaskId = 0;

	public HungerGame(String name) {
		this(name, null);
	}

	public HungerGame(final String name, final String setup) {
		stats = new TreeMap<String, PlayerStat>();
		spawnsTaken = new HashMap<String, Location>();
		allPlayers = new HashSet<String>();
		spawnPoints = new ArrayList<Location>();
		sponsors = new HashMap<String, String>();
		spectatorSponsoringRunnable = new SpectatorSponsoringRunnable(this);
		isRunning = isCounting = isPaused = false;
		randomLocs = new ArrayList<Location>();
		
		chests = new ArrayList<Location>();
		fixedChests = new HashMap<Location, String>();
		this.name = name;
		this.setup = null;
		itemsets = new ArrayList<String>();
		worlds = new HashSet<String>();
		cuboids = new HashSet<Cuboid>();
		enabled = true;
		spawn = null;

		readyToPlay = new ArrayList<String>();
		playerLocs = new HashMap<String, Location>();
		spectators = new HashMap<String, Location>();
		spectatorFlying = new HashMap<String, Boolean>();
		spectatorFlightAllowed = new HashMap<String, Boolean>();
		playerGameModes = new HashMap<String, GameMode>();
		countdown = null;
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
				if (loc == null || loc.getWorld() == null) {
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

		if (section.contains("fixedchests")) {
			ConfigurationSection fixedChestsSection = section.getConfigurationSection("fixedchests");
			for (String key : fixedChestsSection.getKeys(false)) {
				String str = fixedChestsSection.getString(key);
				String[] split = str.split(",");
				if (split.length != 2) continue;
				Location loc = null;
				try {
					loc = HungerGames.parseToLoc(split[0]);
				}
				catch (NumberFormatException e) {
				}
				if (loc == null) {
					Logging.warning("failed to load location '%s'", str);
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Logging.warning("'%s' is no longer a chest.", str);
					continue;
				}
				fixedChests.put(loc, split[1]);
			}

		}
                
                if(section.isList("itemsets")) {
			itemsets.clear();
			itemsets.addAll(section.getStringList("itemsets"));
                }
		
                if(section.isList("worlds")) {
			worlds.clear();
			worlds.addAll(section.getStringList("worlds"));
                }
		if (section.isList("cuboids")) {
			List<Cuboid> cuboidList = new ArrayList<Cuboid>();
			for (String s : section.getStringList("cuboids")) {
				cuboidList.add(Cuboid.parseFromString(s));
			}
			cuboids.clear();
			cuboids.addAll(cuboidList);
		}
		enabled = section.getBoolean("enabled", true);
		if (section.contains("setup")) setup = section.getString("setup");
		try {
			if (section.contains("spawn")) spawn = HungerGames.parseToLoc(section.getString("spawn"));
		} 
		catch (NumberFormatException numberFormatException) {}
		HungerGames.callEvent(new GameLoadEvent(this));
	}

	public void saveTo(ConfigurationSection section) {
		ConfigurationSection spawnPointsSection = section.createSection("spawn-points");
		ConfigurationSection chestsSection = section.createSection("chests");
		ConfigurationSection fixedChestsSection = section.createSection("fixedchests");
		
		for (int cntr = 0; cntr < spawnPoints.size(); cntr++) {
			Location loc = spawnPoints.get(cntr);
			if (loc == null) continue;
			//Logging.debug("Saving a spawnpoint. It's location is: " + loc);
			spawnPointsSection.set("spawnpoint" + (cntr + 1), HungerGames.parseToString(loc));
		}
		
		for (int cntr = 0; cntr < chests.size(); cntr++) {
			Location loc = chests.get(cntr);
			chestsSection.set("chest" + (cntr + 1), HungerGames.parseToString(loc));
		}
		
		Logging.debug("FixedChest size when saving: %s", fixedChests.size());
		int cntr = 0;
		for (Location loc : fixedChests.keySet()) {
			Logging.debug("Saving a fixedchest with index %s", cntr + 1);
			fixedChestsSection.set("fixedchest" + (cntr + 1), HungerGames.parseToString(loc) + "," + fixedChests.get(loc));
			cntr++;
		}
		section.set("itemsets", itemsets);
		if (!worlds.isEmpty()) {
			section.set("worlds", worlds);
			List<String> cuboidStringList = new ArrayList<String>();
			for (Cuboid c : cuboids) {
				cuboidStringList.add(c.parseToString());
			}
		}
		if (!cuboids.isEmpty()) {
			section.set("cuboids", cuboids);
		}
		section.set("enabled", enabled);
		section.set("setup", setup);
		section.set("spawn", HungerGames.parseToString(spawn));
		
		HungerGames.callEvent(new GameSaveEvent(this));
	}

	public void run() {
		if (!isRunning) return;
		Random rand = HungerGames.getRandom();
		Location loc = getRemainingPlayers().get(rand.nextInt(getRemainingPlayers().size())).getLocation();
		if (randomLocs.size() >= 15) randomLocs.remove(rand.nextInt(15));
		randomLocs.add(loc);
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
			ChatUtils.error(player, "%s is already a running game.", name);
			return false;
		}
		if(isPaused) {
			ChatUtils.error(player, "%s has been paused.", name);
			return false;
		}
		readyToPlay.add(player.getName());
		String mess = Config.getVoteMessage(setup).replace("<player>", player.getName()).replace("<game>", this.name);
		ChatUtils.broadcast(mess, true);
		int minVote = Config.getMinVote(setup);
		if ((readyToPlay.size() >= minVote && stats.size() >= Config.getMinPlayers(setup) && !Config.getAllVote(setup))
		    || (readyToPlay.size() >= stats.size() && Config.getAllVote(setup) && !Config.getAutoVote(setup))) {
			ChatUtils.broadcast(true, "Enough players have voted that they are ready. Starting game...", this.name);
			startGame(false);
		}
		return true;
	}

	public void addSpectator(Player player, Player spectated) {
		spectators.put(player.getName(), player.getLocation());
		if (Config.getSpectatorSponsorPeriod(setup) != 0) {
			 spectatorSponsoringRunnable.addSpectator(player);
		}
		Random rand = HungerGames.getRandom();
		Location loc = randomLocs.get(rand.nextInt(randomLocs.size()));
		if (spectated != null) loc = spectated.getLocation();
		player.teleport(loc);
		spectatorFlying.put(player.getName(), player.isFlying());
		spectatorFlightAllowed.put(player.getName(), player.getAllowFlight());
		player.setFlying(true);
		player.setAllowFlight(true);
		for (Player p : getRemainingPlayers()) {
			p.hidePlayer(player);
		}
	}

	public boolean isSpectating(Player player) {
		return spectators.containsKey(player.getName());
	}

	public void removeSpectator(Player player) {
		spectatorSponsoringRunnable.removeSpectator(player);
		player.teleport(spectators.remove(player.getName()));
		player.setFlying(spectatorFlying.get(player.getName()));
		player.setAllowFlight(spectatorFlightAllowed.get(player.getName()));
		for (Player p : getRemainingPlayers()) {
			p.showPlayer(player);
		}
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
			return String.format("%s is currently not enabled.", name);
		}
		isRunning = false;
		for (Player player : getRemainingPlayers()) {
			ItemStack[] contents = player.getInventory().getContents();
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (ItemStack i : contents) {
				if (i != null) list.add(i);
			}
			contents = list.toArray(new ItemStack[list.size()]);
			playerLeaving(player, false);
			teleportPlayerToSpawn(player);
			if (isFinished && Config.getWinnerKeepsItems(setup)) {
				for (ItemStack i : player.getInventory().addItem(contents).values()) {
					player.getLocation().getWorld().dropItem(player.getLocation(), i);
				}
			}
			if (isFinished) HungerGames.rewardPlayer(player);
			StatHandler.updateStat(stats.get(player.getName()));// TODO: this might be a little slow to do it this way. Thread?
		}
		for (String spectatorName : spectators.keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorName);
			removeSpectator(spectator);
		}
		spectatorSponsoringRunnable.cancel();
		HungerGames.cancelTask(locTaskId);
		if (Config.getRemoveItems(setup)) removeItemsOnGround();
		if (!isFinished) {
			GameStopEvent event = new GameStopEvent(this);
			HungerGames.callEvent(event);
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
	 * @param includeCheck 
	 * @return true if game or countdown was successfully started
	 */
	public boolean startGame(Player player, int ticks, boolean includeCheck) {
		if (ticks <= 0) {
			String result = startGame(0, includeCheck);
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
		if(!immediate) return startGame(player, Config.getDefaultTime(setup), true);
		return startGame(player, 0, true);
	}

	/**
	 * Starts this game with the default time if immediate is true. Otherwise, starts the game immediately.
	 * 
	 * @param immediate
	 * @return
	 */
	public boolean startGame(boolean immediate) {
		if(!immediate) return startGame(Config.getDefaultTime(setup), true) == null;
		return startGame(0, true) == null;
	}
	
	/**
	 * Starts the game
	 * 
	 * @param ticks 
	 * @param includeCheck 
	 * @return Null if game or countdown was successfully started. Otherwise, error message.
	 */
	public String startGame(int ticks, boolean includeCheck) {
		if (includeCheck) {
			String result = startGamePreCheck();
			if (result != null) return result;
		}
		
		if (ticks > 0) {
			countdown = new GameCountdown(this, ticks);
			isCounting = true;
			return null;
		}
		
		GameStartEvent event = new GameStartEvent(this);
		HungerGames.callEvent(event);
		if (event.isCancelled()) {
			return "Start was cancelled.";
		}
		locTaskId = HungerGames.scheduleTask(this, 20 * 120, 20 * 10); // Wait two minutes, then poll every 10 seconds
		spectatorSponsoringRunnable.setTaskId(HungerGames.scheduleTask(spectatorSponsoringRunnable, 0, SpectatorSponsoringRunnable.pollEveryInTicks));
		ResetHandler.gameStarting(this);
		releasePlayers();
		fillInventories();
		for (String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
			stats.get(playerName).setPlaying(true);
		}
		isRunning = true;
		isPaused = false;
		readyToPlay.clear();
		ChatUtils.broadcast(true, "Starting %s. Go!!", name);
		return null;
	}
	
	private String startGamePreCheck() {
		if (isRunning) return "Game is already running";
		if (stats.size() < Config.getMinPlayers(setup) || stats.size() < 2) return String.format("There are not enough players in %s", name);
		if (isCounting) return String.format("%s is already counting down.", name);
		if (!enabled) return String.format("%s is currently not enabled.", name);
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
	
	public boolean resumeGame(boolean immediate) {
		if (!immediate) return resumeGame(Config.getDefaultTime(setup)) == null;
		return resumeGame(0) == null;
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
		for(String playerName : playerLocs.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			playerEntering(p, enabled);
			InventorySave.loadGameInventory(p);
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		isRunning = true;
		isPaused = false;
		isCounting = false;
		countdown = null;

		ChatUtils.broadcast(true, "Resuming %s. Go!!", name);
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
		
		isRunning = false;
		isCounting = false;
		isPaused = true;
		if(countdown != null) {
			countdown.cancel();
			countdown = null;
		}
		for(Player p : getRemainingPlayers()) {
			if (p == null) continue;
			playerLocs.put(p.getName(), p.getLocation());
			InventorySave.saveAndClearGameInventory(p);
			playerLeaving(p, true);
			teleportPlayerToSpawn(p);
		}
		for (String spectatorName : spectators.keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorName);
			removeSpectator(spectator);
		}
		HungerGames.callEvent(new GamePauseEvent(this));
		return null;
	}
	
	private void releasePlayers() {
		for (String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			GameManager.unfreezePlayer(p);
		}

	}

	public void addAndFillChest(Chest chest) {
		if (fixedChests.containsKey(chest.getLocation())) return;
		if(!chests.contains(chest.getLocation())) {
			//Logging.debug("Inventory Location was not in randomInvs.");
			HungerGames.fillChest(chest, itemsets);
			addChest(chest.getLocation());
		}
	}
        
	public void fillInventories() {
	    Location prev = null;
	    Logging.debug("Filling inventories. Chests size: %s fixedChests size: %s", chests.size(), fixedChests.size());
	    for (Location loc : chests) {
		    if (prev != null && prev.getBlock().getFace(loc.getBlock()) != null) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    if (!(loc.getBlock().getState() instanceof Chest)) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    prev = loc;
		    Chest chest = (Chest) loc.getBlock().getState();
		    HungerGames.fillChest(chest, itemsets);
	    }
	    for (Location loc : fixedChests.keySet()) {
		    if (prev != null && prev.getBlock().getFace(loc.getBlock()) != null) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    if (!(loc.getBlock().getState() instanceof Chest)) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    prev = loc;
		    Chest chest = (Chest) loc.getBlock().getState();
		    HungerGames.fillFixedChest(chest, fixedChests.get(loc));   
	    }

	}
	
	/**
	 * Only used for players that have left the game, but not quitted. Only valid while game is running
	 * 
	 * @param player
	 * @return true if successful
	 */
	public synchronized boolean rejoin(Player player) {
		if (!isRunning) {
			ChatUtils.error(player, "Game is not running!");
			return false;
		}
		if(!playerEnteringPreCheck(player)) return false;
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
		if (!playerEntering(player, false)) return false;
		stats.get(player.getName()).setPlaying(true);
		
		String mess = Config.getRejoinMessage(setup);
		mess = mess.replace("<player>", player.getName()).replace("<game>", name);
		ChatUtils.broadcast(mess, true);
		return true;
	}

	public synchronized boolean join(Player player) {
	    if (GameManager.getSession(player) != null) {
		    ChatUtils.error(player, "You are already in a game. Leave that game before joining another.");
		    return false;
	    }
	    if (stats.containsKey(player.getName())) {
		    ChatUtils.error(player, "You are already in this game.");
		    return false;
	    }
	    if (!playerEnteringPreCheck(player)) return false;
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
	    if(!playerEntering(player, false)) return false;
	    stats.put(player.getName(), new PlayerStat(player));
	    String mess = Config.getJoinMessage(setup);
	    mess = mess.replace("<player>", player.getName()).replace("<game>", name);
	    ChatUtils.broadcast(mess, true);
	    if (isRunning) {
		    stats.get(player.getName()).setPlaying(true);
	    }
	    else {
		    if (Config.getAutoVote(setup)) addReadyPlayer(player);
	    }
	    return true;
	}

	private synchronized boolean playerEnteringPreCheck(Player player) {
	    if (!enabled) {
		    ChatUtils.error(player, "%s is currently not enabled.", name);
		    return false;
	    }

	    if (spawnsTaken.size() >= spawnPoints.size()) {
		    ChatUtils.error(player, "%s is already full.", name);
		    return false;
	    }

	    if (Config.getRequireInvClear(setup)) {
		    if(!HungerGames.hasInventoryBeenCleared(player)) {
			    ChatUtils.error(player, "You must clear your inventory first (Be sure you're not wearing armor either).");
			    return false;
		    }
	    }
	    return true;
	}

	/**
	 * When a player enters the game. Does not handle stats.
	 * This handles the teleporting.
	 * @param player
	 * @param fromTemporary if the player leaving was temporary. Leave is not temporary.
	 * @return
	 */
	private synchronized boolean playerEntering(Player player, boolean fromTemporary) {
	    Location loc = null;
	    if (!fromTemporary) {
		    loc = getNextOpenSpawnPoint();
		    spawnsTaken.put(player.getName(), loc);
	    }
	    else {
		    loc = spawnsTaken.get(player.getName());
		    allPlayers.add(player.getName());
	    }
	    GameManager.addSubscribedPlayer(player);
	    player.teleport(loc);
	    if(!Config.getRequireInvClear(setup)) InventorySave.saveAndClearInventory(player);
	    if (!isRunning && Config.getFreezePlayers(setup)) GameManager.freezePlayer(player);
	    if (Config.getForceSurvival(setup)) {
		    playerGameModes.put(player.getName(), player.getGameMode());
		    player.setGameMode(GameMode.SURVIVAL);
	    }
	    for (String string : spectators.keySet()) {
		    Player spectator = Bukkit.getPlayer(string);
		    if (spectator == null) continue;
		    player.hidePlayer(spectator);
	    }
	    return true;
	}
	
	public Location getNextOpenSpawnPoint() {
		Random rand = HungerGames.getRandom();
		Location loc;
		do {
			loc = spawnPoints.get(rand.nextInt(spawnPoints.size()));
			if (loc == null) spawnPoints.remove(loc);
			
		} while (loc == null || spawnsTaken.containsValue(loc));
		return loc;
	}
	
	public synchronized boolean leave(Player player) {
		if (!isRunning) return quit(player);
		
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
		playerLeaving(player, false);
		checkForGameOver(false);

		HungerGames.callEvent(new PlayerLeaveGameEvent(this, player));
		String mess = Config.getLeaveMessage(setup);
		mess = mess.replace("<player>", player.getName()).replace("<game>", name);
		ChatUtils.broadcast(mess, true);
		return true;
	}
	
	public synchronized boolean quit(Player player) {
	    if (!contains(player)) {
		ChatUtils.error(player, "You are not in the game %s.", name);
		return false;
	    }
	    boolean wasPlaying = stats.get(player.getName()).isPlaying();
	    if (wasPlaying) {
		    dropInventory(player);
	    }
	    if(isRunning) {
		    stats.get(player.getName()).die();
	    }
	    else {
		    stats.remove(player.getName());
	    }
	    playerLeaving(player, false);
	    if (wasPlaying || !isRunning) {
		    teleportPlayerToSpawn(player);
	    }
	    checkForGameOver(false);

	    HungerGames.callEvent(new PlayerQuitGameEvent(this, player));
	    String mess = Config.getQuitMessage(setup);
	    mess = mess.replace("<player>", player.getName()).replace("<game>", name);
	    ChatUtils.broadcast(mess, true);
	    return true;
	}
	
	/**
	 * Used when a player is exiting.
	 * This does not handle teleporting and should be used before the teleport.
	 * @param player
	 */
	private synchronized void playerLeaving(Player player, boolean temporary) {
		if (playerGameModes.containsKey(player.getName())) {
			player.setGameMode(playerGameModes.remove(player.getName()));
		}
		for (String string : spectators.keySet()) {
		    Player spectator = Bukkit.getPlayer(string);
		    if (spectator == null) continue;
		    player.showPlayer(spectator);
		}
		GameManager.unfreezePlayer(player);
		InventorySave.loadInventory(player);
		if (!temporary) {
			spawnsTaken.remove(player.getName());
		}
	}

	// Complete clear just to be sure
	private void clear() {
		stats.clear();
		spawnsTaken.clear();
		allPlayers.clear();
		isRunning = false;
		isCounting = false;
		isPaused = false;
		spectators.clear();
		sponsors.clear();
		randomLocs.clear();
		
		readyToPlay.clear();
		playerLocs.clear();
		spectatorFlying.clear();
		spectatorFlightAllowed.clear();
		playerGameModes.clear();
		countdown = null;
	}

	/**
	 * Will be canceled if player is playing and teleporting is not allowed
	 * @param player
	 */
	public void teleportPlayerToSpawn(Player player) {
		if (player == null) {
			return;
		}
		if (spawn != null) {
			player.teleport(spawn);
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
		if (!isRunning) return false;
		List<Player> remaining = getRemainingPlayers();
		if (remaining.size() < 2) {
			Player winner = null;
			if (!remaining.isEmpty()) {
				winner = remaining.get(0);
			}
			GameEndEvent event;
			if (winner == null) {
				ChatUtils.broadcast("Strangely, there was no winner left.", true);
				event = new GameEndEvent(this);
			} else {
				ChatUtils.broadcast(true, "%s has won the game %s! Congratulations!", winner.getName(), name);
				event = new GameEndEvent(this, winner);
			}
			stopGame(true);
			HungerGames.callEvent(event);
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
	    ChatUtils.broadcastRaw(mess, ChatColor.WHITE, true);
	    return false;
	}

	public String getInfo() {
		return String.format("%s[%d/%d] Enabled: %b", name, spawnsTaken.size(), spawnPoints.size(), enabled);
	}

	/**
	 * Checks if players are in the game and have lives, regardless is game is running and if they are playing.
	 * @param players players to check
	 * @return
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
	 * @return true if players are in the game, have lives, and are playing
	 */
	public boolean isPlaying(Player... players) {
	    for (Player player : players) {
		if (!isRunning ||
			!stats.containsKey(player.getName()) 
			|| stats.get(player.getName()).hasRunOutOfLives() 
			|| !stats.get(player.getName()).isPlaying()) {
		    return false;
		}
	    }
	    return true;
	}

	public void killed(Player killer, Player killed) {
		if (!isRunning || stats.get(killed.getName()).hasRunOutOfLives()) return;

		PlayerStat killedStat = stats.get(killed.getName());
		PlayerKillEvent event;
		if (killer != null) {
			PlayerStat killerStat = stats.get(killer.getName());
			killerStat.kill(killed.getName());
			String message = Config.getKillMessage(setup).replace("<killer>", killer.getName()).replace("<killed>", killed.getName()).replace("<game>", name);
			event = new PlayerKillEvent(this, killer, killed, message);
			ChatUtils.broadcast(message, true);
			killedStat.death(killer.getName());
		}
		else {
			event = new PlayerKillEvent(this, killed);
			killedStat.death(PlayerStat.NODODY);
		}

		if (killedStat.hasRunOutOfLives()) {
			playerLeaving(killed, false);
			checkForGameOver(false);
		}
		else {
			if (Config.shouldRespawnAtSpawnPoint(setup)) {
				Location respawn = spawnsTaken.get(killed.getName());
				GameManager.addPlayerRespawn(killed, respawn);
			}
			else {
				Location respawn = randomLocs.get(HungerGames.getRandom().nextInt(randomLocs.size()));
				GameManager.addPlayerRespawn(killed, respawn);
			}
			ChatUtils.send(killed, "You have " + killedStat.getLivesLeft() + " lives left.");
		}
		HungerGames.callEvent(event);
	}

	public void killed(Player killed) {
		killed(null, killed);
	}
	
	/**
	 * Gets the players that have lives and are playing
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
			if (stat.hasRunOutOfLives()) {
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
		if (chests.contains(loc) || fixedChests.containsKey(loc)) return false;
		chests.add(loc);
		Block b = loc.getBlock();
		if (b.getRelative(BlockFace.NORTH) instanceof Chest) chests.add(b.getRelative(BlockFace.NORTH).getLocation());
		else if (b.getRelative(BlockFace.SOUTH) instanceof Chest) chests.add(b.getRelative(BlockFace.NORTH).getLocation());
		else if (b.getRelative(BlockFace.EAST) instanceof Chest) chests.add(b.getRelative(BlockFace.NORTH).getLocation());
		else if (b.getRelative(BlockFace.WEST) instanceof Chest) chests.add(b.getRelative(BlockFace.NORTH).getLocation());
		return true;
	}

	public boolean addFixedChest(Location loc, String fixedChest) {
		if (loc == null || fixedChest == null || fixedChest.equalsIgnoreCase("")) return false;
		if (fixedChests.keySet().contains(loc)) return false;
		if (!(loc.getBlock().getState() instanceof Chest)) return false;
		removeChest(loc);
		fixedChests.put(loc, fixedChest);
		return true;
	}

	public boolean addSpawnPoint(Location loc) {
		if (loc == null) return false;
		if (spawnPoints.contains(loc)) return false;
		spawnPoints.add(loc);
		return true;
	}

	/**
	 * Removes chest from fixedChests and adds it to chests
	 * @param loc
	 * @return
	 */
	public boolean removeFixedChest(Location loc) {
		if (loc == null) return false;
		if (!(loc.getBlock().getState() instanceof Chest)) return false;
		fixedChests.remove(loc);
		return chests.add(loc);
	}

	public boolean removeChest(Location loc) {
		Block b = loc.getBlock();
		Location ad = null;
		if (b.getRelative(BlockFace.NORTH) instanceof Chest) loc = b.getRelative(BlockFace.NORTH).getLocation();
		else if (b.getRelative(BlockFace.SOUTH) instanceof Chest) loc = b.getRelative(BlockFace.NORTH).getLocation();
		else if (b.getRelative(BlockFace.EAST) instanceof Chest) loc = b.getRelative(BlockFace.NORTH).getLocation();
		else if (b.getRelative(BlockFace.WEST) instanceof Chest) loc = b.getRelative(BlockFace.NORTH).getLocation();
		if (ad != null) {
			chests.remove(ad);
			fixedChests.remove(ad);
		}
		return chests.remove(loc) || fixedChests.remove(loc) != null;
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
							"Your spawn point has been recently removed. Try rejoining by typing '/hg rejoin %s'", name);
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
	
	public void setCounting(boolean counting) {
		isCounting = counting;
	}
	
	public void addWorld(World world) {
		worlds.add(world.getName());
	}

	public void addCuboid(Location one, Location two) {
		cuboids.add(new Cuboid(one, two));
	}

	public Map<String, String> getSponsors() {
		return Collections.unmodifiableMap(sponsors);
	}

	public void addSponsor(String player, String playerToBeSponsored) {
		sponsors.put(player, playerToBeSponsored);
	}
	
	public Set<World> getWorlds() {
		Set<World> list = new HashSet<World>();
		for (String s : worlds) {
			if (Bukkit.getWorld(s) == null) continue;
			list.add(Bukkit.getWorld(s));
		}
	return list;
	}
	
	public Set<Cuboid> getCuboids() {
		return Collections.unmodifiableSet(cuboids);
	}
	
	public void removeItemsOnGround() {
		for (String s : worlds) {
			World w = Bukkit.getWorld(s);
			if (w == null) continue;
			for (Entity e : w.getEntities()) {
				if (!(e instanceof Item)) continue;
				e.remove();
			}
		}
		for (Cuboid c : cuboids) {
			if (worlds.contains(c.getLower().getWorld().getName())) continue;
			for (Entity e : c.getLower().getWorld().getEntities()) {
				if (!(e instanceof Item)) continue;
				if (!c.isLocationWithin(e.getLocation())) continue;
				e.remove();
			}
		}
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