package me.kitskub.hungergames.games;

import com.google.common.base.Stopwatch;
import me.kitskub.hungergames.api.event.GameSaveEvent;
import me.kitskub.hungergames.api.event.GameEndEvent;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.api.event.PlayerKilledEvent;
import me.kitskub.hungergames.api.event.PlayerLeaveGameEvent;
import me.kitskub.hungergames.api.event.PlayerJoinGameEvent;
import me.kitskub.hungergames.api.event.GameLoadEvent;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.ItemConfig;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.GameCountdown;
import me.kitskub.hungergames.WorldNotFoundException;
import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.api.Game;
import static me.kitskub.hungergames.api.Game.GameState.*;
import static me.kitskub.hungergames.stats.PlayerStat.PlayerState;
import me.kitskub.hungergames.reset.ResetHandler;
import me.kitskub.hungergames.stats.PlayerStat;
import me.kitskub.hungergames.listeners.TeleportListener;
import me.kitskub.hungergames.register.HGPermission;
import me.kitskub.hungergames.stats.GameStats;
import me.kitskub.hungergames.stats.PlayerStat.Team;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.Cuboid;
import me.kitskub.hungergames.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;

	
public class HungerGame implements Runnable, Game {
	// Per game
	private final List<User> playing;
	private final List<User> users;
	//private final Map<String, PlayerStat> stats;
	private final Map<String, Location> spawnsTaken;
	private final List<Location> randomLocs;
	private final Map<String, List<String>> sponsors; // Just a list for info, <sponsor, sponsee>
	private final SpectatorSponsoringRunnable spectatorSponsoringRunnable;
	private final PlayerLightningRunnable playerLightningRunnable;
	private final GracePeriodEndedRunnable gracePeriodEndedRunnable;
	private final List<Team> teams;
	private long startTime;
	private long endTime;

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
	private GameStats gameStats;

	public HungerGame(String name) {
		this(name, null);
	}

	public HungerGame(final String name, final String setup) {
		playing = new ArrayList<User>();
		users = new ArrayList<User>();
		//stats = new TreeMap<String, PlayerStat>();
		spawnsTaken = new HashMap<String, Location>();
		sponsors = new HashMap<String, List<String>>();
		spectatorSponsoringRunnable = new SpectatorSponsoringRunnable(this);
		playerLightningRunnable = new PlayerLightningRunnable(this);
		gracePeriodEndedRunnable = new GracePeriodEndedRunnable(this);
		randomLocs = new ArrayList<Location>();
		teams = new ArrayList<Team>();
		startTime = 0;
		
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
		Location loc = getRemainingPlayers().get(rand.nextInt(size)).getPlayer().getLocation();
		if (randomLocs.size() >= 15) randomLocs.remove(rand.nextInt(15));
		randomLocs.add(loc);
	}
	
	public int compareTo(Game game) {
		return game.getName().compareToIgnoreCase(getName());
	}

	public boolean addReadyPlayer(Player player) {
		if (readyToPlay.contains(player.getName())) {
			ChatUtils.error(player, "You have already cast your vote that you are ready to play.");
			return false;
		}
		if (state == COUNTING_FOR_START) {
			ChatUtils.error(player, Lang.getAlreadyCountingDown(setup).replace("<game>", name));
			return false;
		}
		if (state == RUNNING) {
			ChatUtils.error(player, Lang.getRunning(setup).replace("<game>", name));
			return false;
		}
		readyToPlay.add(player.getName());
		String mess = Lang.getVoteMessage(setup).replace("<player>", player.getName()).replace("<game>", this.name);
		ChatUtils.broadcast(this, mess);
		int minVote = Config.MIN_VOTE.getInt(setup);
		int minPlayers = Config.MIN_PLAYERS.getInt(setup);
		int startTimer = Config.START_TIMER.getInt(setup);
		int ready = readyToPlay.size();
		int joined = users.size();
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
		for (Iterator<User> it = users.iterator(); it.hasNext();) {
			User user = it.next();
			if (!user.getState().equals(PlayerState.WAITING)) continue;
			user.setState(PlayerState.NOT_IN_GAME);
			Player player = user.getPlayer();
			ItemStack[] contents = player.getInventory().getContents();
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (ItemStack i : contents) {
				if (i != null) list.add(i);
			}
			contents = list.toArray(new ItemStack[list.size()]);
			for (ItemStack i : contents) player.getLocation().getWorld().dropItem(player.getLocation(), i);
			playerLeaving(player);
			teleportUserToSpawn(user);
			user.leaveGame();
			users.remove(user);
		}
	}

	public boolean addSpectator(Player player, Player spectated) {
		if (HungerGames.getInstance().getGameManager().getSpectating(player) != null) {
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
		User.get(player).setGameIn(this, User.GameInEntry.Type.SPECTATING);
		for (User p : getRemainingPlayers()) {
			p.getPlayer().hidePlayer(player);
		}
		ChatUtils.send(player, "You are now spectating %s", name);
		return true;
	}

	@Override
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
		User.get(player).leaveGame();
		for (User p : getRemainingPlayers()) {
			p.getPlayer().showPlayer(player);
		}
	}
	
	@Override
	public boolean stopGame(CommandSender cs, boolean isFinished) {
		String result = stopGame(isFinished);
		if (result != null && cs != null) {
			ChatUtils.error(cs, result);
			return false;
		}
		return true;
	}
	
	private static ItemStack[] filterNulls(ItemStack[] stack) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack i : stack) {
			if (i != null) list.add(i); // Remove all null elements
		}
		return list.toArray(new ItemStack[list.size()]);
	}
	
	private static ItemStack[] removeAll(ItemStack[] start, ItemStack[] toRemove) {
		List<ItemStack> list = Arrays.asList(start);
		list.removeAll(Arrays.asList(toRemove));
		return list.toArray(new ItemStack[list.size()]);
	}

	@Override
	public String stopGame(boolean isFinished) {
		clearWaitingPlayers();
		if (state != RUNNING && state != COUNTING_FOR_START) return "Game is not started";
		
		HungerGames.TimerManager.start();
		endTime = System.currentTimeMillis();
		if (countdown != null) countdown.cancel();
		for (User user : getRemainingPlayers()) {
			user.setState(PlayerState.NOT_IN_GAME);
			ItemStack[] contents = filterNulls(user.getPlayer().getInventory().getContents());
			ItemStack[] armor = filterNulls(user.getPlayer().getInventory().getArmorContents());
			Location dropLoc = user.getPlayer().getLocation();
			playerLeaving(user.getPlayer());
			teleportUserToSpawn(user);
			// If we didn't clear inventory before the game, don't drop inventory items
			if (!Config.CLEAR_INV.getBoolean(setup)) {
				contents = removeAll(contents, user.getPlayer().getInventory().getContents());
				armor = removeAll(contents, user.getPlayer().getInventory().getArmorContents());

			}
			if (isFinished && Config.WINNER_KEEPS_ITEMS.getBoolean(setup)) {
				for (ItemStack i : user.getPlayer().getInventory().addItem(contents).values()) {
					user.getPlayer().getLocation().getWorld().dropItem(user.getPlayer().getLocation(), i);
				}
				for (ItemStack i : user.getPlayer().getInventory().addItem(armor).values()) {
					user.getPlayer().getLocation().getWorld().dropItem(user.getPlayer().getLocation(), i);
				}
			} else {
				for (ItemStack i : contents) dropLoc.getWorld().dropItem(dropLoc, i);
			}
			if (isFinished) GeneralUtils.rewardPlayer(user.getPlayer());
		}
		for (User u : users) {
			gameStats.addPlayer(u.getStat(this));
			u.leaveGame();
		}
		users.clear();
		gameStats.saveGameData();
		for (String spectatorName : spectators.keySet()) {
			Player spectator = Bukkit.getPlayer(spectatorName);
			if (spectator == null) continue;
			removeSpectator(spectator);
		}
		spectatorSponsoringRunnable.cancel();
		playerLightningRunnable.cancel();
		gracePeriodEndedRunnable.cancel();
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
		gameStats.submit();
		clear();
		ResetHandler.resetChanges(this);
		HungerGames.TimerManager.stop("HungerGame.stopGame");
		return null;
	}

	@Override
	public boolean startGame(CommandSender cs, int ticks) {
		String result = startGame(ticks);
		if (countdown != null) countdown.setStarter(cs);
		if (result != null) {
			ChatUtils.error(cs, result);
			return false;
		}
		return true;
	}

	@Override
	public boolean startGame(CommandSender cs, boolean immediate) {
		if(!immediate) return startGame(cs, Config.DEFAULT_TIME.getInt(setup));
		return startGame(cs, 0);
	}

	@Override
	public boolean startGame(boolean immediate) {
		if(!immediate) return startGame(Config.DEFAULT_TIME.getInt(setup)) == null;
		return startGame(0) == null;
	}

	@Override
	public String startGame(int ticks) {
		if (state == DISABLED) return Lang.getNotEnabled(setup).replace("<game>", name);
		if (state == RUNNING) return Lang.getRunning(setup).replace("<game>", name);
		if (users.size() < Config.MIN_PLAYERS.getInt(setup)) return String.format("There are not enough players in %s", name);
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
		if (users.size() < 2) ChatUtils.broadcast(this, "%s is being started with only one player. This has a high potential to lead to errors.", name);
		startTime = System.currentTimeMillis();
		// TODO Task ticks every second. Maybe split up into multiple tasks for randomLoc, lightning and grace message?		
		locTask = Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), this, 20 * 120, 20 * 10);
		spectatorSponsoringRunnable.setTask(Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), spectatorSponsoringRunnable, 0, 1));
		playerLightningRunnable.setTask(Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), playerLightningRunnable, 0, 20));
		ResetHandler.gameStarting(this);
		releasePlayers();
		fillInventories();
		for (User u : users) {
			Player p = u.getPlayer();
			World world = p.getWorld();
			world.setFullTime(0L);
			p.setHealth(20);
			p.setFoodLevel(20);
			u.setState(PlayerStat.PlayerState.PLAYING);
		}
		state = RUNNING;
		run(); // Add at least one randomLoc
		readyToPlay.clear();
		gameStats = new GameStats(this);
		ChatUtils.broadcast(this, "Starting %s. Go!!", name);
		if(Config.GRACE_PERIOD.getDouble(setup) > 0){
			ChatUtils.broadcast(this, ChatColor.DARK_PURPLE, getGracePeriodStarted(Config.GRACE_PERIOD.getDouble(setup)));
			gracePeriodEndedRunnable.setTask(Bukkit.getScheduler().runTaskTimer(HungerGames.getInstance(), gracePeriodEndedRunnable, 0, 20));
		}
		return null;
	}

	private void releasePlayers() {
		for (User u : users) {
			HungerGames.getInstance().getGameManager().unfreezePlayer(u.getPlayer());
		}
	}

	@Override
	public void addAndFillChest(Chest chest) {
		if (fixedChests.containsKey(chest.getLocation())) return;
		if(!chests.keySet().contains(chest.getLocation()) && !blacklistedChests.contains(chest.getLocation())) {
			//Logging.debug("Inventory Location was not in randomInvs.");
			GeneralUtils.fillChest(chest, 1, itemsets);
			addChest(chest.getLocation(), 1f);
		}
	}
        
	@Override
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

	@Override
	public synchronized boolean join(Player player) {
	    if (User.get(player).getGameInEntry().getGame() != null) {
		    ChatUtils.error(player, "You are already in a game. Leave that game before joining another.");
		    return false;
	    }
	    if (users.contains(User.get(player))) {
		    ChatUtils.error(player, Lang.getInGame(setup).replace("<game>", name));
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
	    if (state == RUNNING && !Config.ALLOW_JOIN_DURING_GAME.getBoolean(setup)) {
		    ChatUtils.error(player, Lang.getRunning(setup).replace("<game>", name));
		    return false;
	    }
	    PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
	    Bukkit.getPluginManager().callEvent(event);
	    if (event.isCancelled()) return false;
	    Location loc = getNextOpenSpawnPoint();
	    spawnsTaken.put(player.getName(), loc);
	    HungerGames.getInstance().getGameManager().addSubscribedPlayer(player, this);
	    ((GameManager) HungerGames.getInstance().getGameManager()).addBackLocation(player);
	    TeleportListener.allowTeleport(player);
	    player.teleport(loc, TeleportCause.PLUGIN);
	    if (state != RUNNING && Config.FREEZE_PLAYERS.getBoolean(setup)) HungerGames.getInstance().getGameManager().freezePlayer(player);
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
	    else InventorySave.saveInventoryNoClear(player);
	    for (String kit : ItemConfig.getKits()) {
		    if (HGPermission.INSTANCE.hasPermission(player, Perm.USER_KIT.getPermission().getName()) || HGPermission.INSTANCE.hasPermission(player, Perm.USER_KIT.getPermission().getName() + "." + kit)) {
			    player.getInventory().addItem(ItemConfig.getKit(kit).toArray(new ItemStack[ItemConfig.getKit(kit).size()]));
		    }
	    }
	    for (String string : spectators.keySet()) {
		    Player spectator = Bukkit.getPlayer(string);
		    if (spectator == null) continue;
		    player.hidePlayer(spectator);
	    }
	    User get = User.get(player);
	    users.add(get);
	    get.setGameIn(this, User.GameInEntry.Type.PLAYING);
	    String mess = Lang.getJoinMessage(setup);
	    mess = mess.replace("<player>", player.getName()).replace("<game>", name);
	    ChatUtils.broadcast(this, mess);
	    if (state == RUNNING) {
		   get.setState(PlayerState.PLAYING);
	    }
	    else {
		    get.setState(PlayerState.WAITING);
		    if (Config.AUTO_VOTE.getBoolean(setup)) addReadyPlayer(player);
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

	@Override
	public synchronized boolean quit(Player p, boolean callEvent) {
	    User user = User.get(p);
	    if (!contains(user)) {
		    ChatUtils.error(user.getPlayer(), Lang.getNotInGame(setup).replace("<game>", name));
		    return false;
	    }
	    if (callEvent)  Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(this, user.getPlayer(), PlayerLeaveGameEvent.Type.QUIT));
	    dropInventory(user.getPlayer());
	    if(state == RUNNING) {
		    user.getStat(this).die();
	    }
	    else {
		    user.leaveGame();
		    users.remove(user);
	    }
	    playerLeaving(user.getPlayer());
	    teleportUserToSpawn(user);
	    String mess = Lang.getQuitMessage(setup);
	    mess = mess.replace("<player>", user.getPlayer().getName()).replace("<game>", name);
	    ChatUtils.broadcast(this, mess);
	    checkForGameOver(false);
	    return true;
	}
	
	/**
	 * Used when a player is exiting.
	 * This does not handle teleporting and should be used before the teleport.
	 * @param player
	 */
	private synchronized void playerLeaving(Player player) {
		HungerGames.TimerManager.start();
		for (String string : spectators.keySet()) {
		    Player spectator = Bukkit.getPlayer(string);
		    if (spectator == null) continue;
		    player.showPlayer(spectator);
		}
		HungerGames.getInstance().getGameManager().unfreezePlayer(player);
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
		spawnsTaken.remove(player.getName());
		PlayerQueueHandler.addPlayer(player);
		HungerGames.getInstance().getGameManager().removedSubscribedPlayer(player, this);
		HungerGames.TimerManager.stop("HungerGame.playerLeaving");
	}

	// Complete clear just to be sure
	public void clear() {
		releasePlayers();
		users.clear();
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

	@Override
	public void teleportUserToSpawn(User user) {
		if (Config.USE_SPAWN.getBoolean(setup)) {
			if (spawn != null) {
				user.getPlayer().teleport(spawn);
				return;
			}
			else {
				ChatUtils.error(user.getPlayer(), "There was no spawn set for %s. Teleporting to back location.", name);
			}
		}
		HungerGames.TimerManager.start();
		Location loc = ((GameManager) HungerGames.getInstance().getGameManager()).getAndRemoveBackLocation(user.getPlayer());
		if (loc != null) {
			user.getPlayer().teleport(loc, TeleportCause.UNKNOWN);
		}
		else {
			ChatUtils.error(user.getPlayer(), "For some reason, there was no back location. Please contact an admin for help.", name);
			user.getPlayer().teleport(user.getPlayer().getWorld().getSpawnLocation(), TeleportCause.UNKNOWN);
		}
		HungerGames.TimerManager.stop("HungerGame.teleportPlayerToSpawn");
	}

	@Override
	public boolean checkForGameOver(boolean notifyOfRemaining) {// TODO config option
		if (state != RUNNING) return false;
		HungerGames.TimerManager.start();
		List<User> remaining = getRemainingPlayers();
		List<Team> teamsLeft = new ArrayList<Team>();
		int left = 0;
		for (User user : remaining) {
			Team team = user.getStat(this).getTeam();
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
				User winner = null;
				if (!remaining.isEmpty()) {
					winner = remaining.get(0);
				}
				if (winner == null) {
					ChatUtils.broadcast(this, Lang.getNoWinner(setup));
					event = new GameEndEvent(this, true);
				} else {
					ChatUtils.broadcast(this, Lang.getWin(setup).replace("<player>", winner.getPlayer().getName()).replace("<game>", name));
					ChatUtils.send(winner.getPlayer(), "Congratulations! You won!");// TODO message
					event = new GameEndEvent(this, winner.getPlayer());
				}
			}
			Bukkit.getPluginManager().callEvent(event);
			stopGame(true);
			HungerGames.TimerManager.stop("HungerGame.checkForGameOver");
			return true;
		}

		if (!notifyOfRemaining) return false;
		String mess = "Remaining players: ";
		for (int cntr = 0; cntr < remaining.size(); cntr++) {
			mess += remaining.get(cntr).getPlayer().getName();
			if (cntr < remaining.size() - 1) {
				mess += ", ";
			}

		}
		ChatUtils.broadcastRaw(this, ChatColor.WHITE, mess);
		return false;
	}

	@Override
	public String getInfo() {
		return String.format("%s[%d/%d] Enabled: %b", name, spawnsTaken.size(), spawnPoints.size(), state != DISABLED);
	}

	@Override
	public boolean contains(User... userArray) {
		for (User user : userArray) {
			if (!users.contains(user)) return false;
			PlayerState pState = user.getState();
			if (pState == PlayerState.NOT_IN_GAME || pState == PlayerState.DEAD) return false;
		}
		return true;
	}
	
	@Override
	public boolean isPlaying(User... userArray) {
		for (User user : userArray) {
			if (state != RUNNING || !users.contains(user) || user.getState() != PlayerState.PLAYING) {
				return false;
			}
		}
		return true;
	}

	
	public void killed(final Player killer, final Player killed, PlayerDeathEvent deathEvent) {
		User killedUser = User.get(killed);
		if (state != RUNNING || killedUser.getState() != PlayerState.PLAYING) return;

		deathEvent.setDeathMessage(null);
		killed.setHealth(20);
		killed.setFoodLevel(20);
		PlayerStat killedStat = killedUser.getStat(this);
		PlayerKilledEvent event;
		if (killer != null) {
			PlayerStat killerStat = User.get(killer).getStat(this);
			killerStat.kill(killed.getName());
			killedStat.death(killer.getName());
			event = new PlayerKilledEvent(this, killed, killer);
		}
		else {
			event = new PlayerKilledEvent(this, killed);
			killedStat.death(PlayerStat.NODODY);
		}
		String deathMessage;
		GameStats.Death death = new GameStats.Death();
		death.setPlayer(killed.getName());
		death.setTime(System.currentTimeMillis() - getStartTime());
		if(killer == null){
			String cause = GeneralUtils.getNonPvpDeathCause(deathEvent);
			deathMessage = getDeathMessage(killed.getName(), cause);
			death.setKiller(null);
			death.setCause(cause.toUpperCase());
		} else {
			deathMessage = getKillMessage(killed.getDisplayName(), killer.getDisplayName());
			death.setKiller(killer.getName());
			String weapon = (killer.getItemInHand() == null) ? "AIR" : killer.getItemInHand().getType().name();
			death.setCause(weapon);
		}
		event.setDeathMessage(deathMessage);
		gameStats.addDeath(death);
				
				
		Bukkit.getPluginManager().callEvent(event);
		if (killedUser.getState() == PlayerState.DEAD) {
			for (ItemStack i : deathEvent.getDrops()) {
				killed.getWorld().dropItemNaturally(killed.getLocation(), i);
			}
			deathEvent.getDrops().clear();
			playerLeaving(killed);
			final ItemStack[] armor = killed.getInventory().getArmorContents();
			final ItemStack[] inventory = killed.getInventory().getContents();
			Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), new Runnable() {
				@Override
				public void run() {
					killed.getInventory().setArmorContents(armor);
					killed.getInventory().setContents(inventory);
				}

			});

			Location strikeLocation = killed.getLocation();
			strikeLocation.setY(1); // Let lightning hit near bedrock so we don't hurt anyone
			teleportUserToSpawn(killedUser);
			int deathCannon = Config.DEATH_CANNON.getInt(setup);
			int deathMessages = Config.SHOW_DEATH_MESSAGES.getInt(setup);
			if (deathCannon == 1 || deathCannon == 2){
				playCannonBoom();
				killed.getWorld().strikeLightning(strikeLocation);
			}
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
			Location strikeLocation = killed.getLocation();
			strikeLocation.setY(1); // Let lightning hit near bedrock so we don't hurt anyone
			killed.teleport(respawn, TeleportCause.PLUGIN);
			if (Config.DEATH_CANNON.getInt(setup) == 1){
				playCannonBoom();
				killed.getWorld().strikeLightning(strikeLocation);
			}
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

	private String getDeathMessage(String player, String cause) {
		List<String> messages = Lang.getDeathMessages(setup);
		String message = messages.get(new Random().nextInt(messages.size()));
		message = message.replace("<player>", player);
		message = message.replace("<cause>", Lang.getNonPvPDeathcause(setup, cause));
		message = message.replace("<game>", name);
		return message;
	}
	
	private String getGracePeriodStarted(double time) {
		String message = Lang.getGracePeriodStarted(setup);
		message = message.replace("<time>", GeneralUtils.formatTime((int) Config.GRACE_PERIOD.getDouble(setup)));
		return message;
	}

	@Override
	public List<User> getRemainingPlayers() {
		return new ArrayList<User>(playing);
	}

	@Override
	public void listStats(CommandSender cs) {
		int living = 0, dead = 0;
		String mess = "";
		for (int cntr = 0; cntr < users.size(); cntr++) {
			User get = users.get(cntr);
			PlayerStat stat = get.getStat(this);
			String statName;
			if (get.getState() == PlayerState.DEAD) {
				statName = ChatColor.RED.toString() + get.getPlayer().getName() + ChatColor.GRAY.toString();
				dead++;
			} else {
				statName = ChatColor.GREEN.toString() + get.getPlayer().getName() + ChatColor.GRAY.toString();
				living++;
			}
			mess += String.format("%s [%d/%d]", statName, stat.getLivesLeft(), stat.getKills().size());
			if (users.size() >= cntr + 1) {
				mess += ", ";
			}
		}
		ChatUtils.send(cs, "<name>[lives/kills]");
		ChatUtils.send(cs, "Total Players: %s Total Living: %s Total Dead or Not Playing: %s", users.size(), living, dead);
		ChatUtils.send(cs, "");
		ChatUtils.send(cs, mess);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
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

	@Override
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

	@Override
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
	@Override
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

	@Override
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
			return true;
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
	@Override
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
						ChatUtils.error(Bukkit.getPlayer(playerName), "Your spawn point has been recently removed. Try joining the next game.", name);
						quit(Bukkit.getPlayer(playerName), true);
					}
				}
				return true;
			}
		}
		return false;
	}

	private static void dropInventory(Player player) {
		HungerGames.TimerManager.start();
		for (ItemStack i : player.getInventory().getContents()) {
			if (i == null || i.getType().equals(Material.AIR)) continue;
			player.getWorld().dropItemNaturally(player.getLocation(), i);
		}
		player.getInventory().clear();
		HungerGames.TimerManager.stop("HungerGame.dropInventory");
	}

	@Override
	public void setEnabled(boolean flag) {
		if (!flag) {
			stopGame(false);
			clear();
			state = DISABLED;
		}
		if (flag && state == DISABLED) state = STOPPED;
	}

	@Override
	public void setSpawn(Location newSpawn) {
		spawn = newSpawn;
	}

	@Override
	public List<String> getAllPlayers() {
		List<String> list = new ArrayList<String>();
		for (User u : users) {
			list.add(u.getPlayer().getName());
		}
		return list;
	}

	@Override
	public List<User> getUsers() {
		return new ArrayList<User>(users);
	}

	@Override
	public Location getSpawn() {
		return spawn;
	}

	@Override
	public String getSetup() {
		return (setup == null || "".equals(setup)) ? null : setup;
	}

	@Override
	public List<String> getItemSets() {
		return itemsets;
	}

	@Override
	public void addItemSet(String name) {
		itemsets.add(name);
	}

	@Override
	public void removeItemSet(String name) {
		itemsets.remove(name);
	}
	
	public void setDoneCounting() {
		state = ABOUT_TO_START;
	}
	
	@Override
	public void addWorld(World world) {
		worlds.add(world.getName());
	}

	@Override
	public void addCuboid(Location one, Location two) {
		cuboids.add(new Cuboid(one, two));
	}

	@Override
	public Map<String, List<String>> getSponsors() {
		return Collections.unmodifiableMap(sponsors);
	}

	public void addSponsor(String player, String playerToBeSponsored) {
		if (sponsors.get(player) == null) sponsors.put(player, new ArrayList<String>());
		sponsors.get(player).add(playerToBeSponsored);
	}
	
	@Override
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
	
	@Override
	public Set<Cuboid> getCuboids() {
		return Collections.unmodifiableSet(cuboids);
	}
	
	@Override
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
			if (worlds.contains(c.getWorld())) continue;
			for (Entity e : c.getWorld().getEntities()) {
				if (!(e instanceof Item)) continue;
				if (!c.isLocationWithin(e.getLocation())) continue;
				e.remove();
			}
		}
	}
	
	@Override
	public int getSize() {
		return spawnPoints.size();
	}

	@Override
	public void playCannonBoom() {
		for (User p : getRemainingPlayers()) {
			p.getPlayer().getWorld().createExplosion(p.getPlayer().getLocation(), 0f, false);
		}
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public GameState getState() {
		return state;
	}

	@Override
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

	@Override
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