package com.randude14.hungergames.games;

import com.randude14.hungergames.*;
import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.api.Game;
import static com.randude14.hungergames.api.Game.GameState.*;
import static com.randude14.hungergames.stats.PlayerStat.PlayerState;
import com.randude14.hungergames.reset.ResetHandler;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.listeners.TeleportListener;
import com.randude14.hungergames.register.HGPermission;
import com.randude14.hungergames.stats.PlayerStat.Team;
import com.randude14.hungergames.stats.StatHandler;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.Cuboid;
import com.randude14.hungergames.utils.GeneralUtils;

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

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;

	
public class HungerGame implements Comparable<HungerGame>, Runnable, Game {
	// Per game
	private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final List<Location> randomLocs;
	private final Map<String, List<String>> sponsors; // Just a list for info, <sponsor, sponsee>
	private final SpectatorSponsoringRunnable spectatorSponsoringRunnable;
	private final List<Long> startTimes;
	private final List<Long> endTimes;
	private final List<Team> teams;
	private long initialStartTime;

	// Persistent
	private final Map<Location, Float> chests;
	private final Map<Location, String> fixedChests;
	private final List<Location> blacklistedChests;
	private final List<Location> spawnPoints;
	private final String name;
	private String setup;
	private final List<String> itemsets;
	private final Set<String> worlds;
	private final Set<Cuboid> cuboids;
	private Location spawn;
	private GameState state;

	
	// Temporary
	private final Map<String, Location> playerLocs;// For pausing
	private final Map<String, Location> spectators;
	private final Map<String, Boolean> spectatorFlying; // If a spectator was flying
	private final Map<String, Boolean> spectatorFlightAllowed; // If a spectator's flight was allowed
	private final Map<String, GameMode> playerGameModes; // Whether a player was in survival when game started
	private final List<String> playersFlying; // Players that were flying when they joined
	private final List<String> playersCanFly; // Players that could fly when they joined
	private final List<String> readyToPlay;
	private GameCountdown countdown;
	private BukkitTask locTask;

	public HungerGame(String name) {
		this(name, null);
	}

	public HungerGame(final String name, final String setup) {
		stats = new TreeMap<String, PlayerStat>();
		spawnsTaken = new HashMap<String, Location>();
		sponsors = new HashMap<String, List<String>>();
		spectatorSponsoringRunnable = new SpectatorSponsoringRunnable(this);
		randomLocs = new ArrayList<Location>();
		startTimes = new ArrayList<Long>();
		endTimes = new ArrayList<Long>();
		teams = new ArrayList<Team>();
		initialStartTime = 0;
		
		chests = new HashMap<Location, Float>();
		fixedChests = new HashMap<Location, String>();
		blacklistedChests = new ArrayList<Location>();
		spawnPoints = new ArrayList<Location>();
		this.name = name;
		this.setup = null;
		itemsets = new ArrayList<String>();
		worlds = new HashSet<String>();
		cuboids = new HashSet<Cuboid>();
		spawn = null;
		state = GameState.STOPPED;

		readyToPlay = new ArrayList<String>();
		playerLocs = new HashMap<String, Location>();
		spectators = new HashMap<String, Location>();
		spectatorFlying = new HashMap<String, Boolean>();
		spectatorFlightAllowed = new HashMap<String, Boolean>();
		playerGameModes = new HashMap<String, GameMode>();
		playersFlying = new ArrayList<String>();
		playersCanFly = new ArrayList<String>();
		countdown = null;
	}

	public void loadFrom(ConfigurationSection section) {
		spawnPoints.clear();
		chests.clear();
		fixedChests.clear();
		itemsets.clear();
		worlds.clear();
		cuboids.clear();
		if (section.contains("spawn-points")) {
			ConfigurationSection spawnPointsSection = section.getConfigurationSection("spawn-points");
			for (String key : spawnPointsSection.getKeys(false)) {
				String str = spawnPointsSection.getString(key);
				Location loc = null;
				try {
					loc = GeneralUtils.parseToLoc(str);
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (NumberFormatException e) {
					Logging.debug(e.getMessage());
					continue;
				}
				spawnPoints.add(loc);
			}

		}

		if (section.contains("chests")) {
			ConfigurationSection chestsSection = section.getConfigurationSection("chests");
			for (String key : chestsSection.getKeys(false)) {
				String[] parts = chestsSection.getString(key).split(",");
				Location loc = null;
				float weight = 1f;
				try {
					loc = GeneralUtils.parseToLoc(parts[0]);
					weight = Float.parseFloat(parts[1]);
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (NumberFormatException e) {
					Logging.debug(e.getMessage());
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Logging.warning("'%s' is no longer a chest.", parts[0]);
					continue;
				}
				chests.put(loc, weight);
			}

		}

		if (section.contains("blacklistedchests")) {
			ConfigurationSection chestsSection = section.getConfigurationSection("blacklistedchests");
			for (String key : chestsSection.getKeys(false)) {
				Location loc;
				try {
					loc = GeneralUtils.parseToLoc(chestsSection.getString(key));
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (NumberFormatException e) {
					Logging.debug(e.getMessage());
					continue;
				}
				if (!(loc.getBlock().getState() instanceof Chest)) {
					Logging.warning("'%s' is no longer a chest.", chestsSection.getString(key));
					continue;
				}
				blacklistedChests.add(loc);
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
					loc = GeneralUtils.parseToLoc(split[0]);
				} catch (WorldNotFoundException ex) {
					Logging.warning(ex.getMessage());
					continue;
				} catch (NumberFormatException e) {
					Logging.debug(e.getMessage());
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
			itemsets.addAll(section.getStringList("itemsets"));
                }
		
                if(section.isList("worlds")) {
			worlds.addAll(section.getStringList("worlds"));
                }
		if (section.isList("cuboids")) {
			List<Cuboid> cuboidList = new ArrayList<Cuboid>();
			for (String s : section.getStringList("cuboids")) {
				cuboidList.add(Cuboid.parseFromString(s));
			}
			cuboids.addAll(cuboidList);
		}
		setEnabled(section.getBoolean("enabled", true));
		if (section.contains("setup")) setup = section.getString("setup");
		try {
			if (section.contains("spawn")) spawn = GeneralUtils.parseToLoc(section.getString("spawn"));
		} catch (WorldNotFoundException ex) {
			Logging.warning(ex.getMessage());
		} catch (NumberFormatException e) {
			Logging.debug(e.getMessage());
		}
		 Bukkit.getPluginManager().callEvent(new GameLoadEvent(this));
	}

	public void saveTo(ConfigurationSection section) {
		ConfigurationSection spawnPointsSection = section.createSection("spawn-points");
		ConfigurationSection chestsSection = section.createSection("chests");
		ConfigurationSection blacklistedchestsSection = section.createSection("blacklistedchests");
		ConfigurationSection fixedChestsSection = section.createSection("fixedchests");
		int cntr;
		
		for (cntr = 0; cntr < spawnPoints.size(); cntr++) {
			Location loc = spawnPoints.get(cntr);
			if (loc == null) continue;
			String parsed = GeneralUtils.parseToString(loc);
			//Logging.debug("Saving a spawnpoint. It's location is: " + loc + "\n" + "Parsed as: " + parsed);
			spawnPointsSection.set("spawnpoint" + (cntr + 1), parsed);
		}
		cntr = 1;
		for (Location loc : chests.keySet()) {
			chestsSection.set("chest" + cntr, GeneralUtils.parseToString(loc) + "," + chests.get(loc));
			cntr++;
		}
		cntr = 1;
		for (Location loc : blacklistedChests) {
			blacklistedchestsSection.set("chest" + cntr, GeneralUtils.parseToString(loc));
			cntr++;
		}
		
		cntr = 1;
		for (Location loc : fixedChests.keySet()) {
			fixedChestsSection.set("fixedchest" + cntr, GeneralUtils.parseToString(loc) + "," + fixedChests.get(loc));
			cntr++;
		}
		section.set("itemsets", itemsets);
		if (!worlds.isEmpty()) {
			section.set("worlds", new ArrayList<String>(worlds));
		}
		List<String> cuboidStringList = new ArrayList<String>();
		for (Cuboid c : cuboids) {
			cuboidStringList.add(c.parseToString());
		}
		if (!cuboidStringList.isEmpty()) {
			section.set("cuboids", cuboidStringList);
		}
		section.set("enabled", state != DISABLED);
		section.set("setup", setup);
		section.set("spawn", GeneralUtils.parseToString(spawn));
		
		 Bukkit.getPluginManager().callEvent(new GameSaveEvent(this));
	}

	public void run() {
		if (state != RUNNING) return;
		Random rand = HungerGames.getRandom();
		int size = getRemainingPlayers().size();
		if (size < 0) {
			Logging.debug("HungerGame.run(): Unexpected size:" + size);
			return;
		}
		Location loc = getRemainingPlayers().get(rand.nextInt(size)).getLocation();
		if (randomLocs.size() >= 15) randomLocs.remove(rand.nextInt(15));
		randomLocs.add(loc);
	}
	
	public int compareTo(HungerGame game) {
		return game.name.compareToIgnoreCase(name);
	}

	public boolean addReadyPlayer(Player player) {
		if (state == DELETED) {
			ChatUtils.error(player, "That game does not exist anymore.");
			return false;
		}
		if (readyToPlay.contains(player.getName())) {
			ChatUtils.error(player, "You have already cast your vote that you are ready to play.");
			return false;
		}
		if (state == COUNTING_FOR_RESUME || state == COUNTING_FOR_START) {
			ChatUtils.error(player, Lang.getAlreadyCountingDown(setup).replace("<game>", name));
			return false;
		}
		if (state == RUNNING) {
			ChatUtils.error(player, Lang.getRunning(setup).replace("<game>", name));
			return false;
		}
		if(state == PAUSED) {
			ChatUtils.error(player, "%s has been paused.", name);
			return false;
		}
		readyToPlay.add(player.getName());
		String mess = Lang.getVoteMessage(setup).replace("<player>", player.getName()).replace("<game>", this.name);
		ChatUtils.broadcast(this, mess);
		int minVote = Config.MIN_VOTE.getInt(setup);
		int minPlayers = Config.MIN_PLAYERS.getInt(setup);
		int startTimer = Config.START_TIMER.getInt(setup);
		int ready = readyToPlay.size();
		int joined = stats.size();
		boolean allVote = Config.ALL_VOTE.getBoolean(setup);
		boolean autoVote = Config.AUTO_VOTE.getBoolean(setup);
		if (joined >= minPlayers) {
			if ((ready >= minVote && !allVote) || (ready >= joined && allVote && !autoVote)) {
				ChatUtils.broadcast(this, "Enough players have voted that they are ready. Starting game...", this.name);
				startGame(false);
			}
			else if (startTimer > 0) {
				ChatUtils.broadcast(this, "The minimum amount of players for this game has been reached. Countdown has begun...", this.name);
				startGame(startTimer);
			}
		}
		return true;
	}
	
	public void clearWaitingPlayers() {
		for (Iterator<String> it = stats.keySet().iterator(); it.hasNext();) {
			String stat = it.next();
			if (!stats.get(stat).getState().equals(PlayerState.WAITING)) continue;
			stats.get(stat).setState(PlayerState.NOT_IN_GAME);
			Player player = Bukkit.getPlayer(stat);
			ItemStack[] contents = player.getInventory().getContents();
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (ItemStack i : contents) {
				if (i != null) list.add(i);
			}
			contents = list.toArray(new ItemStack[list.size()]);
			playerLeaving(player, false);
			for (ItemStack i : contents) player.getLocation().getWorld().dropItem(player.getLocation(), i);
			teleportPlayerToSpawn(player);
			GameManager.INSTANCE.clearGamesForPlayer(stat, this);
			stats.remove(stat);
		}
	}

	public boolean addSpectator(Player player, Player spectated) {
		if (GameManager.INSTANCE.getSpectating(player) != null) {
			ChatUtils.error(player, "You cannot spectate while in a game.");
			return false;
		}
		if (state != RUNNING) {
			ChatUtils.error(player, Lang.getNotRunning(setup).replace("<game>", name));
			return false;
		}
		spectators.put(player.getName(), player.getLocation());
		if (Config.SPECTATOR_SPONSOR_PERIOD.getInt(setup) != 0) {
			 spectatorSponsoringRunnable.addSpectator(player);
		}
		Random rand = HungerGames.getRandom();
		Location loc = randomLocs.get(rand.nextInt(randomLocs.size()));
		if (spectated != null) loc = spectated.getLocation();
		player.teleport(loc);
		spectatorFlying.put(player.getName(), player.isFlying());
		spectatorFlightAllowed.put(player.getName(), player.getAllowFlight());
		player.setAllowFlight(true);
		player.setFlying(true);
		for (Player p : getRemainingPlayers()) {
			p.hidePlayer(player);
		}
		ChatUtils.send(player, "You are now spectating %s", name);
		return true;
	}

	
	public boolean isSpectating(Player player) {
		return spectators.containsKey(player.getName());
	}

	public void removeSpectator(Player player) {
		if (!spectators.containsKey(player.getName())) {
			ChatUtils.error(player, "You are not spectating that game.");
			return;
		}
		spectatorSponsoringRunnable.removeSpectator(player);
		player.setFlying(spectatorFlying.get(player.getName()));
		player.setAllowFlight(spectatorFlightAllowed.get(player.getName()));
		player.teleport(spectators.remove(player.getName()));
		for (Player p : getRemainingPlayers()) {
			p.showPlayer(player);
		}
	}
	
	
	public boolean stopGame(CommandSender cs, boolean isFinished) {
		String result = stopGame(isFinished);
		if (result != null && cs != null) {
			ChatUtils.error(cs, result);
			return false;
		}
		return true;
	}
	
	
	public String stopGame(boolean isFinished) {
		if (state == DELETED) return "That game does not exist anymore.";
		clearWaitingPlayers();
		if (state != RUNNING && state != PAUSED && state != COUNTING_FOR_RESUME && state != COUNTING_FOR_START) return "Game is not started";
		
		endTimes.add(System.currentTimeMillis());
		if (countdown != null) countdown.cancel();
		if (state == PAUSED) { // Needed for inventory stuff
			for(String playerName : playerLocs.keySet()) {
				Player p = Bukkit.getPlayer(playerName);
				if (p == null) continue;
				playerEntering(p, true);
				InventorySave.loadGameInventory(p);
			}
		}
		StatHandler.updateGame(this);
		for (Player player : getRemainingPlayers()) {
			stats.get(player.getName()).setState(PlayerState.NOT_IN_GAME);
			ItemStack[] contents = player.getInventory().getContents();
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (ItemStack i : contents) {
				if (i != null) list.add(i); // Remove all null elements
			}
			contents = list.toArray(new ItemStack[list.size()]);
			playerLeaving(player, false);
			if (isFinished && Config.WINNER_KEEPS_ITEMS.getBoolean(setup)) {
				for (ItemStack i : player.getInventory().addItem(contents).values()) {
					player.getLocation().getWorld().dropItem(player.getLocation(), i);
				}
			}
			else {
				for (ItemStack i : contents) player.getLocation().getWorld().dropItem(player.getLocation(), i);
			}
			teleportPlayerToSpawn(player);
			if (isFinished) GeneralUtils.rewardPlayer(player);
		}
		for (String stat : stats.keySet()) {
			StatHandler.updateStat(stats.get(stat));// TODO: this might be a little slow to do it this way. Thread?
			GameManager.INSTANCE.clearGamesForPlayer(stat, this);
		}
		stats.clear();
		for (String spectatorName : spectators.keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorName);
			if (spectator == null) continue;
			removeSpectator(spectator);
		}
		spectatorSponsoringRunnable.cancel();
		if (locTask != null) {
			locTask.cancel();
			locTask = null;
		}
		if (Config.REMOVE_ITEMS.getBoolean(setup)) removeItemsOnGround();
		state = STOPPED;
		if (!isFinished) {
			GameEndEvent event = new GameEndEvent(this, false);
			Bukkit.getPluginManager().callEvent(event);
		}
		clear();
		ResetHandler.resetChanges(this);
		return null;
	}

	
	public boolean startGame(CommandSender cs, int ticks) {
		String result = startGame(ticks);
		if (countdown != null) countdown.setStarter(cs);
		if (result != null) {
			ChatUtils.error(cs, result);
			return false;
		}
		return true;
	}

	
	public boolean startGame(CommandSender cs, boolean immediate) {
		if(!immediate) return startGame(cs, Config.DEFAULT_TIME.getInt(setup));
		return startGame(cs, 0);
	}

	
	public boolean startGame(boolean immediate) {
		if(!immediate) return startGame(Config.DEFAULT_TIME.getInt(setup)) == null;
		return startGame(0) == null;
	}

	
	public String startGame(int ticks) {
		if (state == DELETED) return "Game no longer exists.";
		if (state == DISABLED) return Lang.getNotEnabled(setup).replace("<game>", name);
		if (state == RUNNING) return Lang.getRunning(setup).replace("<game>", name);
		if (stats.size() < Config.MIN_PLAYERS.getInt(setup)) return String.format("There are not enough players in %s", name);
		if (countdown != null) {
			if (ticks < countdown.getTimeLeft()) {
				countdown.cancel();
				countdown = null;
			}
			else {
				return Lang.getAlreadyCountingDown(setup).replace("<game>", name);
			}
		}
		if (ticks > 0) {
			countdown = new GameCountdown(this, ticks);
			state = COUNTING_FOR_START;
			return null;
		}
		GameStartEvent event = new GameStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return "Start was cancelled.";
		}
		if (stats.size() < 2) ChatUtils.broadcast(this, "%s is being started with only one player. This has a high potential to lead to errors.", name);
		initialStartTime = System.currentTimeMillis();
		startTimes.add(System.currentTimeMillis());
		locTask = Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), this, 20 * 120, 20 * 10);
		spectatorSponsoringRunnable.setTask(Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), spectatorSponsoringRunnable, 0, 1));
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
			stats.get(playerName).setState(PlayerStat.PlayerState.PLAYING);
		}
		state = RUNNING;
		run(); // Add at least one randomLoc
		readyToPlay.clear();
		ChatUtils.broadcast(this, "Starting %s. Go!!", name);
		return null;
	}

	
 	public boolean resumeGame(CommandSender cs, int ticks) {		
		if (ticks <= 0) {
			String result = resumeGame(0);
			if (result != null) {
				ChatUtils.error(cs, result);
				return false;
			}
		} else {
			countdown = new GameCountdown(this, ticks, true);
			state = COUNTING_FOR_RESUME;
		}
		return true;
	}
	
	
	public boolean resumeGame(CommandSender cs, boolean immediate) {
		if (!immediate) return resumeGame(cs, Config.DEFAULT_TIME.getInt(setup));
		return resumeGame(cs, 0);
	}
	
	
	public boolean resumeGame(boolean immediate) {
		if (!immediate) return resumeGame(Config.DEFAULT_TIME.getInt(setup)) == null;
		return resumeGame(0) == null;
	}

	
	public String resumeGame(int ticks) {
		if (state == DELETED) return "That game does not exist anymore.";
		if(state != PAUSED && state != ABOUT_TO_START) return "Cannot resume a game that has not been paused.";
		if (ticks > 0) {
			countdown = new GameCountdown(this, ticks, true);
			state = COUNTING_FOR_RESUME;
			return null;
		}
		startTimes.add(System.currentTimeMillis());
		GameStartEvent event = new GameStartEvent(this, true);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return "Start was cancelled.";
		}
		for(String playerName : playerLocs.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			stats.get(p.getName()).setState(PlayerState.PLAYING);
			playerEntering(p, true);
			InventorySave.loadGameInventory(p);
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		state = RUNNING;
		countdown = null;
		ChatUtils.broadcast(this, "Resuming %s. Go!!", name);
		return null;
	}
	
	
	public boolean pauseGame(CommandSender cs) {
		String result = pauseGame();
		if (result != null) {
			ChatUtils.error(cs, "Cannot pause a game that has been paused.");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return null if successful, message if not
	 */
	
	public String pauseGame() {
		if (state == DELETED) return "That game does not exist anymore.";
		if(state == PAUSED) return "Cannot pause a game that has been paused.";
		
		state = PAUSED;
		endTimes.add(System.currentTimeMillis());
		if(countdown != null) {
			countdown.cancel();
			countdown = null;
		}
		for(Player p : getRemainingPlayers()) {
			if (p == null) continue;
			stats.get(p.getName()).setState(PlayerState.GAME_PAUSED);
			playerLocs.put(p.getName(), p.getLocation());
			InventorySave.saveAndClearGameInventory(p);
			playerLeaving(p, true);
			teleportPlayerToSpawn(p);
		}
		for (String spectatorName : spectators.keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorName);
			removeSpectator(spectator);
		}
		Bukkit.getPluginManager().callEvent(new GamePauseEvent(this));
		return null;
	}
	
	private void releasePlayers() {
		for (String playerName : stats.keySet()) {
			Player p = Bukkit.getPlayer(playerName);
			if (p == null) continue;
			GameManager.INSTANCE.unfreezePlayer(p);
		}

	}

	
	public void addAndFillChest(Chest chest) {
		if (fixedChests.containsKey(chest.getLocation())) return;
		if(!chests.keySet().contains(chest.getLocation()) && !blacklistedChests.contains(chest.getLocation())) {
			//Logging.debug("Inventory Location was not in randomInvs.");
			GeneralUtils.fillChest(chest, 0, itemsets);
			addChest(chest.getLocation(), 1f);
		}
	}
        
	
	public void fillInventories() {
	    Location prev = null;
	    // Logging.debug("Filling inventories. Chests size: %s fixedChests size: %s", chests.size(), fixedChests.size());
	    for (Location loc : chests.keySet()) {
		    if (prev != null && prev.getBlock().getFace(loc.getBlock()) != null) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    if (!(loc.getBlock().getState() instanceof Chest)) {
			    //Logging.debug("Cancelling a fill because not a chest");
			    continue;
		    }
		    prev = loc;
		    Chest chest = (Chest) loc.getBlock().getState();
		    GeneralUtils.fillChest(chest, chests.get(loc), itemsets);
	    }
	    for (Location loc : fixedChests.keySet()) {
		    if (prev != null && prev.getBlock().getFace(loc.getBlock()) != null) {
			    //Logging.debug("Cancelling a fill because previous was a chest");
			    continue;
		    }
		    if (!(loc.getBlock().getState() instanceof Chest)) {
			    //Logging.debug("Cancelling a fill because not a chest");
			    continue;
		    }
		    prev = loc;
		    Chest chest = (Chest) loc.getBlock().getState();
		    GeneralUtils.fillFixedChest(chest, fixedChests.get(loc));   
	    }

	}

	
	public synchronized boolean rejoin(Player player) {
		if (state != RUNNING) {
			ChatUtils.error(player, Lang.getNotRunning(setup).replace("<game>", name));
			return false;
		}
		if(!playerEnteringPreCheck(player)) return false;
		if (!Config.ALLOW_REJOIN.getBoolean(setup)) {
			ChatUtils.error(player, "You are not allowed to rejoin a game.");
			return false;
		}
		if (stats.get(player.getName()).getState() == PlayerState.PLAYING){
			ChatUtils.error(player, "You can't rejoin a game while you are in it.");
			return false;
		}
		if (!stats.containsKey(player.getName()) || stats.get(player.getName()).getState() != PlayerState.NOT_PLAYING) {
			ChatUtils.error(player, Lang.getNotInGame(setup).replace("<game>", name));
			return false;
		}
		PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player, true);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		if (!playerEntering(player, false)) return false;
		stats.get(player.getName()).setState(PlayerState.PLAYING);
		
		String mess = Lang.getRejoinMessage(setup);
		mess = mess.replace("<player>", player.getName()).replace("<game>", name);
		ChatUtils.broadcast(this, mess);
		return true;
	}

	
	public synchronized boolean join(Player player) {
	    if (GameManager.INSTANCE.getSession(player) != null) {
		    ChatUtils.error(player, "You are already in a game. Leave that game before joining another.");
		    return false;
	    }
	    if (stats.containsKey(player.getName())) {
		    ChatUtils.error(player, Lang.getInGame(setup).replace("<game>", name));
		    return false;
	    }
	    if (!playerEnteringPreCheck(player)) return false;
	    if (state == RUNNING && !Config.ALLOW_JOIN_DURING_GAME.getBoolean(setup)) {
		    ChatUtils.error(player, Lang.getRunning(setup).replace("<game>", name));
		    return false;
	    }
	    if(state == PAUSED) {
		    ChatUtils.error(player, "%s has been paused.", name);
		    return false;
	    }
	    PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
	    Bukkit.getPluginManager().callEvent(event);
	    if (event.isCancelled()) return false;
	    if(!playerEntering(player, false)) return false;
	    stats.put(player.getName(), GameManager.INSTANCE.createStat(this, player));
	    String mess = Lang.getJoinMessage(setup);
	    mess = mess.replace("<player>", player.getName()).replace("<game>", name);
	    ChatUtils.broadcast(this, mess);
	    if (state == RUNNING) {
		    stats.get(player.getName()).setState(PlayerState.PLAYING);
	    }
	    else {
		    stats.get(player.getName()).setState(PlayerState.WAITING);
		    if (Config.AUTO_VOTE.getBoolean(setup)) addReadyPlayer(player);
	    }
	    return true;
	}

	private synchronized boolean playerEnteringPreCheck(Player player) {
	    if (state == DELETED) {
		    ChatUtils.error(player, "That game does not exist anymore.");
		    return false;
	    }
	    if (state == DISABLED) {
		    ChatUtils.error(player, Lang.getNotEnabled(setup).replace("<game>", name));
		    return false;
	    }

	    if (spawnsTaken.size() >= spawnPoints.size()) {
		    ChatUtils.error(player, "%s is already full.", name);
		    return false;
	    }

	    if (Config.REQUIRE_INV_CLEAR.getBoolean(setup)) {
		    if(!GeneralUtils.hasInventoryBeenCleared(player)) {
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
	    Location loc;
	    if (!fromTemporary) {
		    loc = getNextOpenSpawnPoint();
		    spawnsTaken.put(player.getName(), loc);
	    }
	    else {
		    loc = spawnsTaken.get(player.getName());
	    }
	    GameManager.INSTANCE.addSubscribedPlayer(player, this);
	    GameManager.INSTANCE.addBackLocation(player);
	    TeleportListener.allowTeleport(player);
	    player.teleport(loc, TeleportCause.PLUGIN);
	    if (state != RUNNING && Config.FREEZE_PLAYERS.getBoolean(setup)) GameManager.INSTANCE.freezePlayer(player);
	    if (Config.FORCE_SURVIVAL.getBoolean(setup)) {
		    playerGameModes.put(player.getName(), player.getGameMode());
		    player.setGameMode(GameMode.SURVIVAL);
	    }
	    if (Config.DISABLE_FLY.getBoolean(setup)) {
		    if (!HGPermission.INSTANCE.hasPermission(player, Perm.ADMIN_ALLOW_FLIGHT)) {
			    if (player.getAllowFlight()) {
				    playersCanFly.add(player.getName());
				    player.setAllowFlight(false);
			    }
			    if (player.isFlying()) {
				    playersFlying.add(player.getName());
				    player.setFlying(false);
			    }
			    
		    }
	    }
	    if (Config.HIDE_PLAYERS.getBoolean(setup)) player.setSneaking(true);
	    if (Config.CLEAR_INV.getBoolean(setup)) InventorySave.saveAndClearInventory(player);
	    for (String kit : ItemConfig.getKits()) {
		    if (HGPermission.INSTANCE.hasPermission(player, Perm.USER_KIT.getPermission().getName()) || HGPermission.INSTANCE.hasPermission(player, Perm.USER_KIT.getPermission().getName() + "." + kit)) {
			    player.getInventory().addItem((ItemStack[]) ItemConfig.getKit(kit).toArray());
		    }
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
	
	
	public synchronized boolean leave(Player player, boolean callEvent) {
		if (state != RUNNING && state != PAUSED) return quit(player, true);
		
		if (!isPlaying(player)) {
			ChatUtils.error(player, "You are not playing the game %s.", name);
			return false;
		}

		if (!Config.ALLOW_REJOIN.getBoolean(setup)) {
			stats.get(player.getName()).die();
		}
		else {
			stats.get(player.getName()).setState(PlayerState.NOT_PLAYING);
			stats.get(player.getName()).death(PlayerStat.NODODY);
		}
		if (callEvent) Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, player, PlayerLeaveGameEvent.Type.LEAVE));
		if (state == PAUSED) playerEntering(player, true);
		InventorySave.loadGameInventory(player);
		dropInventory(player);
		playerLeaving(player, false);
		teleportPlayerToSpawn(player);
		String mess = Lang.getLeaveMessage(setup);
		mess = mess.replace("<player>", player.getName()).replace("<game>", name);
		ChatUtils.broadcast(this,mess);
		checkForGameOver(false);

		return true;
	}
	
	
	public synchronized boolean quit(Player player, boolean callEvent) {
	    if (!contains(player)) {
		    ChatUtils.error(player, Lang.getNotInGame(setup).replace("<game>", name));
		    return false;
	    }
	    if (callEvent)  Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, player, PlayerLeaveGameEvent.Type.QUIT));
	    boolean wasPlaying = stats.get(player.getName()).getState() == PlayerState.PLAYING || stats.get(player.getName()).getState() == PlayerState.WAITING;
	    if (wasPlaying) {
		    dropInventory(player);
	    }
	    if(state == RUNNING) {
		    stats.get(player.getName()).die();
	    }
	    else {
		    stats.remove(player.getName());
		    GameManager.INSTANCE.clearGamesForPlayer(player.getName(), this);
	    }
	    playerLeaving(player, false);
	    if (wasPlaying || state != RUNNING) {
		    teleportPlayerToSpawn(player);
	    }
	    
	    String mess = Lang.getQuitMessage(setup);
	    mess = mess.replace("<player>", player.getName()).replace("<game>", name);
	    ChatUtils.broadcast(this, mess);
	    checkForGameOver(false);
	    return true;
	}
	
	/**
	 * Used when a player is exiting.
	 * This does not handle teleporting and should be used before the teleport.
	 * @param player
	 */
	private synchronized void playerLeaving(Player player, boolean temporary) {
		for (String string : spectators.keySet()) {
		    Player spectator = Bukkit.getPlayer(string);
		    if (spectator == null) continue;
		    player.showPlayer(spectator);
		}
		GameManager.INSTANCE.unfreezePlayer(player);
		InventorySave.loadInventory(player);
		if (playerGameModes.containsKey(player.getName())) {
			player.setGameMode(playerGameModes.remove(player.getName()));
		}
		if (Config.DISABLE_FLY.getBoolean(setup)) {
			if (!HGPermission.INSTANCE.hasPermission(player, Perm.ADMIN_ALLOW_FLIGHT)) {
				player.setAllowFlight(playersCanFly.remove(player.getName()));
				player.setFlying(playersFlying.remove(player.getName()));
			}
		}
		if (Config.HIDE_PLAYERS.getBoolean(setup)) player.setSneaking(false);
		readyToPlay.remove(player.getName());
		if (!temporary) {
			spawnsTaken.remove(player.getName());
			PlayerQueueHandler.addPlayer(player);
			GameManager.INSTANCE.removedSubscribedPlayer(player, this);
		}
	}

	// Complete clear just to be sure
	public void clear() {
		releasePlayers();
		stats.clear();
		spawnsTaken.clear();
		spectators.clear();
		sponsors.clear();
		randomLocs.clear();
		
		readyToPlay.clear();
		playerLocs.clear();
		spectatorFlying.clear();
		spectatorFlightAllowed.clear();
		playerGameModes.clear();
		playersCanFly.clear();
		playersFlying.clear();
		if (countdown != null) countdown.cancel(); 
		countdown = null;
	}

	
	public void teleportPlayerToSpawn(Player player) {
		if (player == null) {
			return;
		}
		if (Config.USE_SPAWN.getBoolean(setup)) {
			if (spawn != null) {
				player.teleport(spawn);
				return;
			}
			else {
				ChatUtils.error(player, "There was no spawn set for %s. Teleporting to back location.", name);
			}
		}
		Location loc = GameManager.INSTANCE.getAndRemoveBackLocation(player);
		if (loc != null) {
			player.teleport(loc, TeleportCause.UNKNOWN);
		}
		else {
			ChatUtils.error(player, "For some reason, there was no back location. Please contact an admin for help.", name);
			player.teleport(player.getWorld().getSpawnLocation(), TeleportCause.UNKNOWN);
		}
	}

	
	public boolean checkForGameOver(boolean notifyOfRemaining) {// TODO config option
		if (state != RUNNING) return false;
		List<Player> remaining = getRemainingPlayers();
		List<Team> teamsLeft = new ArrayList<Team>();
		int left = 0;
		for (Player p : remaining) {
			Team team = stats.get(p.getName()).getTeam();
			if (team == null) {
				left++;
			}
			else if (!teamsLeft.contains(team)) {
				teamsLeft.add(team);
				left++;
			}
		}
		if (left < 2) {
			GameEndEvent event = null;
			if (teamsLeft.size() > 0) {
				ChatUtils.sendToTeam(teamsLeft.get(0), "Congratulations! Your team won!");
				event = new GameEndEvent(this, teamsLeft.get(0));
			}
			else {
				Player winner = null;
				if (!remaining.isEmpty()) {
					winner = remaining.get(0);
				}
				if (winner == null) {
					ChatUtils.broadcast(this, Lang.getNoWinner(setup));
					event = new GameEndEvent(this, true);
				} else {
					ChatUtils.broadcast(this, Lang.getWin(setup).replace("<player>", winner.getName()).replace("<game>", name));
					ChatUtils.send(winner, "Congratulations! You won!");// TODO message
					event = new GameEndEvent(this, winner);
				}
			}
			Bukkit.getPluginManager().callEvent(event);
			stopGame(true);
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
		ChatUtils.broadcastRaw(this, ChatColor.WHITE, mess);
		return false;
	}

	
	public String getInfo() {
		return String.format("%s[%d/%d] Enabled: %b", name, spawnsTaken.size(), spawnPoints.size(), state != DISABLED);
	}

	
	public boolean contains(Player... players) {
	    if (state == DELETED) return false;
	    for (Player player : players) {
		if (!stats.containsKey(player.getName())) return false;
		PlayerState pState = stats.get(player.getName()).getState();
		if (pState == PlayerState.NOT_IN_GAME || pState == PlayerState.DEAD) return false;
	    }
	    return true;
	}
	
	
	public boolean isPlaying(Player... players) {
	    for (Player player : players) {
		if (state != RUNNING || !stats.containsKey(player.getName()) 
			|| stats.get(player.getName()).getState() != PlayerState.PLAYING ){
		    return false;
		}
	    }
	    return true;
	}

	
	public void killed(final Player killer, final Player killed, PlayerDeathEvent deathEvent) {
		if (state == DELETED || state != RUNNING || stats.get(killed.getName()).getState() != PlayerState.PLAYING) return;

		deathEvent.setDeathMessage(null);
		killed.setHealth(20);
		killed.setFoodLevel(20);
		PlayerStat killedStat = stats.get(killed.getName());
		PlayerKilledEvent event;
		if (killer != null) {
			PlayerStat killerStat = stats.get(killer.getName());
			killerStat.kill(killed.getName());
			killedStat.death(killer.getName());
			event = new PlayerKilledEvent(this, killed, killer);
		}
		else {
			event = new PlayerKilledEvent(this, killed);
			killedStat.death(PlayerStat.NODODY);
		}
		String deathMessage = killer == null ? getDeathMessage(killed.getName()) : getKillMessage(killed.getDisplayName(), killer.getDisplayName()); 
		event.setDeathMessage(deathMessage);
		Bukkit.getPluginManager().callEvent(event);
		if (killedStat.getState() == PlayerState.DEAD) {
			for (ItemStack i : deathEvent.getDrops()) {
				killed.getWorld().dropItemNaturally(killed.getLocation(), i);
			}
			deathEvent.getDrops().clear();
			playerLeaving(killed, false);
			final ItemStack[] armor = killed.getInventory().getArmorContents();
			final ItemStack[] inventory = killed.getInventory().getContents();
			Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), new Runnable() {
				
				public void run() {
					killed.getInventory().setArmorContents(armor);
					killed.getInventory().setContents(inventory);
				}

			});

			teleportPlayerToSpawn(killed);
			int deathCannon = Config.DEATH_CANNON.getInt(setup);
			int deathMessages = Config.SHOW_DEATH_MESSAGES.getInt(setup);
			if (deathCannon == 1 || deathCannon == 2) playCannonBoom();
			if (deathMessages == 1 || deathMessages == 2) {
				ChatUtils.broadcast(this, event.getDeathMessage());
			}
			checkForGameOver(false);
		}
		else {
			Location respawn;
			if (Config.SPAWNPOINT_ON_DEATH.getBoolean(setup)) {
				respawn = spawnsTaken.get(killed.getName());
			}
			else {
				respawn = randomLocs.get(HungerGames.getRandom().nextInt(randomLocs.size()));
			}
			TeleportListener.allowTeleport(killed);
			killed.teleport(respawn, TeleportCause.PLUGIN);
			if (Config.DEATH_CANNON.getInt(setup) == 1) playCannonBoom();
			if (Config.SHOW_DEATH_MESSAGES.getInt(setup) == 1) {
				ChatUtils.broadcast(this, event.getDeathMessage());
			}
			ChatUtils.send(killed, "You have " + killedStat.getLivesLeft() + " lives left.");
		}
	}

	private String getKillMessage(String killed, String killer) {
		List<String> messages = Lang.getKillMessages(setup);
		String message = messages.get(new Random().nextInt(messages.size()));
		message = message.replace("<killed>", killed);
		message = message.replace("<killer>", killer);
		message = message.replace("<game>", name);
		return message;
	}

	private String getDeathMessage(String player) {
		List<String> messages = Lang.getDeathMessages(setup);
		String message = messages.get(new Random().nextInt(messages.size()));
		message = message.replace("<player>", player);
		message = message.replace("<game>", name);
		return message;
	}

	
	public List<Player> getRemainingPlayers() {
	    List<Player> remaining = new ArrayList<Player>();
	    for (String playerName : stats.keySet()) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) continue;
		PlayerStat stat = stats.get(playerName);
		if (stat.getState() == PlayerState.PLAYING || stat.getState() == PlayerState.GAME_PAUSED || stat.getState() == PlayerState.WAITING) {
		    remaining.add(player);
		}
	    }
	    return remaining;
	}

	
	public PlayerStat getPlayerStat(OfflinePlayer player) {
		return stats.get(player.getName());
	}

	
	public void listStats(CommandSender cs) {
		int living = 0, dead = 0;
		List<String> players = new ArrayList<String>(stats.keySet());
		String mess = "";
		for (int cntr = 0; cntr < players.size(); cntr++) {
			PlayerStat stat = stats.get(players.get(cntr));
			Player p = stat.getPlayer();
			if (p == null) continue;
			String statName;
			if (stat.getState() == PlayerState.DEAD) {
				statName = ChatColor.RED.toString() + p.getName() + ChatColor.GRAY.toString();
				dead++;
			}
			else if (stat.getState() == PlayerState.NOT_PLAYING) {
				statName = ChatColor.YELLOW.toString() + p.getName() + ChatColor.GRAY.toString();
				dead++;
			}
			else {
				statName = ChatColor.GREEN.toString() + p.getName() + ChatColor.GRAY.toString();
				living++;
			}
			mess += String.format("%s [%d/%d]", statName, stat.getLivesLeft(), stat.getKills().size());
			if (players.size() >= cntr + 1) {
				mess += ", ";
			}
		}
		ChatUtils.send(cs, "<name>[lives/kills]");
		ChatUtils.send(cs, "Total Players: %s Total Living: %s Total Dead or Not Playing: %s", stats.size(), living, dead);
		ChatUtils.send(cs, "");
		ChatUtils.send(cs, mess);
	}

	
	public String getName() {
		return name;
	}

	
	public boolean addChest(Location loc, float weight) {
		if (chests.keySet().contains(loc) || fixedChests.containsKey(loc)) return false;
		blacklistedChests.remove(loc);
		chests.put(loc, weight);
		Block b = loc.getBlock();
		if (b.getRelative(BlockFace.NORTH).getState() instanceof Chest) chests.put(b.getRelative(BlockFace.NORTH).getLocation(), weight);
		else if (b.getRelative(BlockFace.SOUTH).getState() instanceof Chest) chests.put(b.getRelative(BlockFace.SOUTH).getLocation(), weight);
		else if (b.getRelative(BlockFace.EAST).getState() instanceof Chest) chests.put(b.getRelative(BlockFace.EAST).getLocation(), weight);
		else if (b.getRelative(BlockFace.WEST).getState() instanceof Chest) chests.put(b.getRelative(BlockFace.WEST).getLocation(), weight);
		return true;
	}

	
	public boolean addFixedChest(Location loc, String fixedChest) {
		if (loc == null || fixedChest == null || fixedChest.equalsIgnoreCase("")) return false;
		if (fixedChests.keySet().contains(loc)) return false;
		blacklistedChests.remove(loc);
		if (!(loc.getBlock().getState() instanceof Chest)) return false;
		removeChest(loc);
		fixedChests.put(loc, fixedChest);
		Block b = loc.getBlock();
		if (b.getRelative(BlockFace.NORTH).getState() instanceof Chest) fixedChests.put(b.getRelative(BlockFace.NORTH).getLocation(), fixedChest);
		else if (b.getRelative(BlockFace.SOUTH).getState() instanceof Chest) fixedChests.put(b.getRelative(BlockFace.SOUTH).getLocation(), fixedChest);
		else if (b.getRelative(BlockFace.EAST).getState() instanceof Chest) fixedChests.put(b.getRelative(BlockFace.EAST).getLocation(), fixedChest);
		else if (b.getRelative(BlockFace.WEST).getState() instanceof Chest) fixedChests.put(b.getRelative(BlockFace.WEST).getLocation(), fixedChest);
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
		Block b = loc.getBlock();
		if (b.getRelative(BlockFace.NORTH).getState() instanceof Chest) fixedChests.remove(b.getRelative(BlockFace.NORTH).getLocation());
		else if (b.getRelative(BlockFace.SOUTH).getState() instanceof Chest) fixedChests.remove(b.getRelative(BlockFace.SOUTH).getLocation());
		else if (b.getRelative(BlockFace.EAST).getState() instanceof Chest) fixedChests.remove(b.getRelative(BlockFace.EAST).getLocation());
		else if (b.getRelative(BlockFace.WEST).getState() instanceof Chest) fixedChests.remove(b.getRelative(BlockFace.WEST).getLocation());
		return addChest(loc, 1f);
	}

	
	public boolean removeChest(Location loc) {
		Block b = loc.getBlock();
		Location ad = null;
		if (b.getRelative(BlockFace.NORTH).getState() instanceof Chest) ad = b.getRelative(BlockFace.NORTH).getLocation();
		else if (b.getRelative(BlockFace.SOUTH).getState() instanceof Chest) ad = b.getRelative(BlockFace.SOUTH).getLocation();
		else if (b.getRelative(BlockFace.EAST).getState() instanceof Chest) ad = b.getRelative(BlockFace.EAST).getLocation();
		else if (b.getRelative(BlockFace.WEST).getState() instanceof Chest) ad = b.getRelative(BlockFace.WEST).getLocation();
		if (ad != null) {
			if (chests.remove(ad) == null & fixedChests.remove(ad) == null) {
				blacklistedChests.add(ad);
			}
		}
		if (chests.remove(loc) == null & fixedChests.remove(loc) == null) {
			blacklistedChests.add(loc);
			return false;
		}
		return true;
	}

	public void chestBroken(Location loc) {
		Block b = loc.getBlock();
		Location ad = null;
		if (b.getRelative(BlockFace.NORTH).getState() instanceof Chest) ad = b.getRelative(BlockFace.NORTH).getLocation();
		else if (b.getRelative(BlockFace.SOUTH).getState() instanceof Chest) ad = b.getRelative(BlockFace.SOUTH).getLocation();
		else if (b.getRelative(BlockFace.EAST).getState() instanceof Chest) ad = b.getRelative(BlockFace.EAST).getLocation();
		else if (b.getRelative(BlockFace.WEST).getState() instanceof Chest) ad = b.getRelative(BlockFace.WEST).getLocation();
		if (ad != null) {
			chests.remove(ad);
			fixedChests.remove(ad);
		}
		chests.remove(loc);
		fixedChests.remove(loc);
	}
	
	public boolean removeSpawnPoint(Location loc) {
		if (loc == null) return false;
		Iterator<Location> iterator = spawnPoints.iterator();
		Location l;
		while (iterator.hasNext()) {
			if (GeneralUtils.equals(loc, l = iterator.next())) {
				iterator.remove();
				for (String playerName : spawnsTaken.keySet()) {
					Location comp = spawnsTaken.get(playerName);
					if (GeneralUtils.equals(l, comp)) {
						spawnsTaken.remove(playerName);
						if (Bukkit.getPlayer(playerName) == null) continue;
						ChatUtils.error(Bukkit.getPlayer(playerName),
							"Your spawn point has been recently removed. Try rejoining by typing '/hg rejoin %s'", name);
						leave(Bukkit.getPlayer(playerName), true);
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
		if (state == DELETED) return;
		if (!flag) {
			stopGame(false);
			clear();
			state = DISABLED;
		}
		if (flag && state == DISABLED) state = STOPPED;
	}

	
	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}

	
	public List<String> getAllPlayers() {
		return new ArrayList<String>(stats.keySet());
	}

	
	public List<PlayerStat> getStats() {
		return new ArrayList<PlayerStat>(stats.values());
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
	
	public void setDoneCounting() {
		state = ABOUT_TO_START;
	}
	
	
	public void addWorld(World world) {
		worlds.add(world.getName());
	}

	
	public void addCuboid(Location one, Location two) {
		cuboids.add(new Cuboid(one, two));
	}

	
	public Map<String, List<String>> getSponsors() {
		return Collections.unmodifiableMap(sponsors);
	}

	public void addSponsor(String player, String playerToBeSponsored) {
		if (sponsors.get(player) == null) sponsors.put(player, new ArrayList<String>());
		sponsors.get(player).add(playerToBeSponsored);
	}
	
	
	public Set<World> getWorlds() {
		if (worlds.size() <= 0) return Collections.emptySet();
		Set<World> list = new HashSet<World>();
		for (String s : worlds) {
			World w = Bukkit.getWorld(s);
			if (w == null) continue;
			list.add(w);
		}
		return list;
	}
	
	
	public Set<Cuboid> getCuboids() {
		return Collections.unmodifiableSet(cuboids);
	}
	
	
	public void removeItemsOnGround() {
		Logging.debug("Aboout the check items on the ground for %s worlds.", worlds.size());
		for (String s : worlds) {
			World w = Bukkit.getWorld(s);
			if (w == null) continue;
			Logging.debug("Checking world for items.");
			int count = 0;
			for (Entity e : w.getEntities()) {
				count++;
				if (!(e instanceof Item)) continue;
				e.remove();
			}
			Logging.debug("Checked: ", count);
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
	
	
	public int getSize() {
		return spawnPoints.size();
	}

	
	public void playCannonBoom() {
		for (Player p : getRemainingPlayers()) {
			p.getWorld().createExplosion(p.getLocation(), 0f, false);
		}
	}

	
	public List<Long> getEndTimes() {
		return endTimes;
	}

	
	public long getInitialStartTime() {
		return initialStartTime;
	}

	
	public List<Long> getStartTimes() {
		return startTimes;
	}
	
	
	public GameState getState() {
		return state;
	}

	public void delete() {
		clear();
		state = DELETED;
		chests.clear();
		fixedChests.clear();
		setup = null;
		itemsets.clear();
		worlds.clear();
		cuboids.clear();
		spawn = null;
	}

	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final HungerGame other = (HungerGame) obj;
		return this.compareTo(other) == 0;
	}

	
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + (this.name != null ? this.name.toLowerCase().hashCode() : 0);
		return hash;
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