package com.randude14.hungergames;

import com.randude14.hungergames.commands.CommandHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.register.BukkitPermission;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.Permission;
import com.randude14.hungergames.register.VaultPermission;

public class Plugin extends JavaPlugin implements Listener {
	private static final Logger logger = Logger.getLogger("Minecraft");
	public static final String CMD_ADMIN = "hga";
	public static final String CMD_USER = "hg";
	private static Plugin instance;
	private static Permission perm;
	private static Economy econ;
	private static GameManager manager;
	private static Random rand;
	private static Map<Player, Location> frozenPlayers;
	private static Map<Player, Session> chestAdders;
	private static Map<Player, Session> chestRemovers;
	private static Map<Player, Session> spawnAdders;
	private static Map<Player, Session> spawnRemovers;
	private static Map<Player, String> sponsors;
	private static Map<ItemStack, Float> globalChestLoot;
	private static Map<ItemStack, Double> globalSponsorLoot;
	private static Map<String, Map<ItemStack, Float>> chestLoots;
	private static Map<String, Map<ItemStack, Double>> sponsorLoots;

	@Override
	public void onEnable() {
		instance = this;
		CommandHandler commands = new CommandHandler();
		getCommand(CMD_USER).setExecutor(commands);
		getCommand(CMD_ADMIN).setExecutor(commands);
		rand = new Random(getName().hashCode());
		manager = new GameManager();
		frozenPlayers = new HashMap<Player, Location>();
		chestAdders = new HashMap<Player, Session>();
		chestRemovers = new HashMap<Player, Session>();
		spawnAdders = new HashMap<Player, Session>();
		spawnRemovers = new HashMap<Player, Session>();
		sponsors = new HashMap<Player, String>();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(manager, this);
		if (!new File(getDataFolder(), "config.yml").exists()) {
			info("config not found. saving defaults.");
			saveDefaultConfig();
		}
		globalChestLoot = Config.getGlobalChestLoot();
		globalSponsorLoot = Config.getGlobalSponsorLoot();
		chestLoots = new HashMap<String, Map<ItemStack, Float>>();
		sponsorLoots = new HashMap<String, Map<ItemStack, Double>>();
		for(String itemset : Config.getItemSets()){
		    chestLoots.put(itemset, Config.getChestLoot(itemset));
		    sponsorLoots.put(itemset, Config.getSponsorLoot(itemset));
		}
		loadRegistry();
		callTasks();
		GameManager.loadGames();
		info("Games loaded.");
		info("Enabled.");
	}

	private void callTasks() {
		Plugin.scheduleTask(new Runnable() {
			public void run() {
				String installedVersion = getDescription().getVersion();
				String checkVersion = updateCheck(installedVersion);
				if (!checkVersion.endsWith(installedVersion))
					Plugin.warning(
							"There is a new version: %s (You are running %s)",
							checkVersion, installedVersion);
			}

		}, 0L, Config.getUpdateDelay() * 20L * 60L);

	}

	@Override
	public void onDisable() {
		GameManager.saveGames();
		info("Games saved.");
		info("Disabled.");
	}
	
	private static void loadRegistry() {
		if (!VaultPermission.isVaultInstalled()) {
			info("Vault is not installed, defaulting to Bukkit perms.");
			perm = new BukkitPermission();
			return;
		}
		else {
			perm = new VaultPermission();
		}
		
		if(!Economy.isVaultInstalled()) {
			warning("Vault is not installed, economy use disabled.");
			econ = null;
		}
		else {
			econ = new Economy();
		}
		
	}

	public static void reload() {
		instance.reloadConfig();
		globalChestLoot = Config.getGlobalChestLoot();
		globalSponsorLoot = Config.getGlobalSponsorLoot();
		chestLoots = new HashMap<String, Map<ItemStack, Float>>();
		sponsorLoots = new HashMap<String, Map<ItemStack, Double>>();
		for(String itemset : Config.getItemSets()){
		    chestLoots.put(itemset, Config.getChestLoot(itemset));
		    sponsorLoots.put(itemset, Config.getSponsorLoot(itemset));
		}
		GameManager.loadGames();
		loadRegistry();
		for (Player player : sponsors.keySet()) {
			error(player,
					"The items available for sponsoring have recently changed. Here are the new items...");
			addSponsor(player, sponsors.remove(player));
		}

	}
	
	public static void info(String format, Object... args) {
		logger.log(Level.INFO, getLogPrefix() + String.format(format, args));
	}

	public static void info(String mess) {
		logger.log(Level.INFO, getLogPrefix() + mess);
	}

	public static void warning(String format, Object... args) {
		logger.log(Level.WARNING, getLogPrefix() + String.format(format, args));
	}

	public static void warning(String mess) {
		logger.log(Level.WARNING, getLogPrefix() + mess);
	}

	public static void severe(String format, Object... args) {
		logger.log(Level.SEVERE, getLogPrefix() + String.format(format, args));
	}

	public static void severe(String mess) {
		logger.log(Level.SEVERE, getLogPrefix() + mess);
	}

	public static String getLogPrefix() {
		return String.format("[%s] v%s - ", instance.getName(), instance
				.getDescription().getVersion());
	}

	public static String getPrefix() {
		return String.format("[%s] - ", instance.getName());
	}

	public static String getHeadLiner() {
		return String.format("--------------------[%s]--------------------",
				instance.getName());
	}

	public static void broadcast(String message) {
		broadcast(message, ChatColor.GREEN);
	}

	public static void broadcast(String format, Object... args) {
		broadcast(String.format(format, args));
	}

	public static void broadcast(ChatColor color, String format, Object... args) {
		broadcast(color, String.format(format, args));
	}

	public static void broadcast(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + getPrefix() + message);
		}

		message = ChatColor.stripColor(message);
		info(message);
	}

	public static void broadcastRaw(String format, Object... args) {
		broadcastRaw(String.format(format, args));
	}

	public static void broadcastRaw(String message) {
		broadcastRaw(message, ChatColor.GREEN);
	}

	public static void broadcastRaw(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + message);
		}

		message = ChatColor.stripColor(message);
		logger.info(message);
	}

	public static void send(Player player, ChatColor color, String format,
			Object... args) {
		player.sendMessage(color + String.format(format, args));
	}

	public static void send(Player player, ChatColor color, String mess) {
		player.sendMessage(color + mess);
	}

	public static void send(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GRAY + String.format(format, args));
	}

	public static void send(Player player, String mess) {
		player.sendMessage(ChatColor.GRAY + mess);
	}

	public static void help(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GOLD + String.format(format, args));
	}

	public static void help(Player player, String mess) {
		player.sendMessage(ChatColor.GOLD + mess);
	}
	
	public static void helpCommand(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GOLD + String.format("- " + format, args));
	}
	
	public static void error(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.RED + String.format(format, args));
	}

	public static void error(Player player, String mess) {
		player.sendMessage(ChatColor.RED + mess);
	}

	public static void sendDoesNotExist(Player player, String s) {
		Plugin.error(player, "%s does not exist.", s);
	}

	public static boolean hasPermission(Player p, Defaults.Perm perm) {
		String world = p.getWorld().getName();
		String player = p.getName();
		String permission = perm.getPermission();
		return Plugin.perm.hasPermission(player, world, permission);
	}

	public static boolean equals(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	public static boolean isEconomyEnabled() {
		return econ != null;
	}
	
	public static void withdraw(Player player, double amount) {
		if(!isEconomyEnabled()) {
			error(player, "Economy use has been disabled.");
			return;
		}
		econ.withdraw(player.getName(), amount);
	}
	
	public static void deposit(Player player, double amount) {
		if(!isEconomyEnabled()) {
			error(player, "Economy use has been disabled.");
			return;
		}
		econ.deposit(player.getName(), amount);
	}
	
	public static boolean hasEnough(Player player, double amount) {
		if(!isEconomyEnabled()) {
			error(player, "Economy use has been disabled.");
			return false;
		}
		return econ.hasEnough(player.getName(), amount);
	}

	public static boolean isChest(Location loc) {
		return loc.getBlock().getState() instanceof Chest;
	}

	public static Random getRandom() {
		return rand;
	}

	public static GameManager getGameManager() {
		return manager;
	}

	public static int scheduleTask(Runnable runnable, long initial, long delay) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance,
				runnable, initial, delay);
	}

	public static void cancelTask(int taskID) {
		Bukkit.getServer().getScheduler().cancelTask(taskID);
	}

	public static void freezePlayer(Player player) {
		frozenPlayers.put(player, player.getLocation());
	}

	public static void unfreezePlayer(Player player) {
		frozenPlayers.remove(player);
	}

	public static boolean isPlayerFrozen(Player player) {
		return frozenPlayers.containsKey(player);
	}

	public static void addChestAdder(Player player, String name) {
		Session session = new Session(name);
		chestAdders.put(player, session);
	}

	public static void addChestRemover(Player player, String name) {
		chestRemovers.put(player, new Session(name));
	}

	public static void addSpawnAdder(Player player, String name) {
		spawnAdders.put(player, new Session(name));
	}

	public static void addSpawnRemover(Player player, String name) {
		spawnRemovers.put(player, new Session(name));
	}

	public static boolean addSponsor(Player player, String playerToBeSponsored) {
		Player sponsoredPlayer = Bukkit.getPlayer(playerToBeSponsored);
		HungerGame game = GameManager.getSession(sponsoredPlayer);
		if(game == null){
		    error(player, "That player is not in a game.");
		    return false;
		}
		List<String> itemsets = game.getItemSets();
		if (globalSponsorLoot.isEmpty() && (itemsets == null || itemsets.isEmpty())) {
			error(player, "No items are available to sponsor.");
			return false;
		}

		if(!isEconomyEnabled()) {
			error(player, "Economy use has been disabled.");
		}
		sponsors.put(player, playerToBeSponsored);
		send(player, ChatColor.GREEN, getHeadLiner());
		send(player, ChatColor.YELLOW,
				"Type the number next to the item you would like sponsor to %s.",
				playerToBeSponsored);
		send(player, "");
		int num = 1;
		Map<ItemStack, Double> itemMap = Config.getAllSponsorLootWithGlobal(itemsets);
		for (ItemStack item : itemMap.keySet()) {
			String mess = String.format(">> %d - %s: %d", num, item
					.getType().name(), item.getAmount());
			Set<Enchantment> enchants = item.getEnchantments().keySet();
			int cntr = 0;
			if (!enchants.isEmpty()) {
				mess += ", ";
			}
			for (Enchantment enchant : enchants) {
				mess += String.format("%s: %d", enchant.getName(),
						item.getEnchantmentLevel(enchant));
				if (cntr < enchants.size() - 1) {
					mess += ", ";
				}
				cntr++;
			}
			send(player, ChatColor.GOLD, mess);
			num++;
		}
		return true;

	}

	public String updateCheck(String currentVersion) {
		try {
			URL url = new URL(
					"http://dev.bukkit.org/server-mods/myhungergames/files.rss");
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement
						.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName
						.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
		}

		return currentVersion;
	}

	public static String parseToString(Location loc) {
		return String.format("%.2f %.2f %.2f %.2f %.2f %s", loc.getX(), loc
				.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc
				.getWorld().getName());
	}

	public static Location parseToLoc(String str) {
		if (str == null) {
			return null;
		}
		String[] strs = str.split(" ");
		double x = Double.parseDouble(strs[0]);
		double y = Double.parseDouble(strs[1]);
		double z = Double.parseDouble(strs[2]);
		float yaw = Float.parseFloat(strs[3]);
		float pitch = Float.parseFloat(strs[4]);
		World world = Bukkit.getServer().getWorld(strs[5]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		if (!frozenPlayers.containsKey(player)
				|| GameManager.getSession(player) == null
				|| GameManager.getSession(player).isRunning()) {
			return;
		}
		Location at = player.getLocation();
		Location loc = frozenPlayers.get(player);
		if (!equals(at, loc)) {
			player.teleport(loc);
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		spawnAdders.remove(player);
		spawnRemovers.remove(player);
		chestAdders.remove(player);
		chestRemovers.remove(player);
		sponsors.remove(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		spawnAdders.remove(player);
		spawnRemovers.remove(player);
		chestAdders.remove(player);
		chestRemovers.remove(player);
		sponsors.remove(player);
	}

	@EventHandler
	public void playerChat(PlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		if (!sponsors.containsKey(player)) {
			return;
		}

		int choice = 0;
		event.setCancelled(true);
		String mess = event.getMessage();
		String sponsor = sponsors.remove(player);
		try {
			choice = Integer.parseInt(mess) - 1;
		} catch (Exception ex) {
			error(player, "'%s' is not an integer.", mess);
			return;
		}
		
		Player beingSponsored = getServer().getPlayer(sponsor);
		if (beingSponsored == null) {
			error(player, "'%s' is not online anymore.", sponsor);
			return;
		}
		HungerGame game = GameManager.getSession(player);
		if (game == null) {
			error(player, "'%s' is no longer in a game.", sponsor);
			return;
		}
		Map<ItemStack, Double> itemMap = Config.getAllSponsorLootWithGlobal(game.getItemSets());
		
		int size = itemMap.size();
		if (choice < 0 || choice >= size) {
			error(player, "Choice '%d' does not exist.");
			return;
		}

		ItemStack item = new ArrayList<ItemStack>(itemMap.keySet())
				.get(choice);
		double price = itemMap.get(item);
		if (!hasEnough(beingSponsored, price)) {
			error(player, String.format("You do not have enough money."));
			return;
		}
		withdraw(player, price);
		if (item.getEnchantments().isEmpty()) {
			send(beingSponsored, "%s has sponsored you %d %s(s).",
					player.getName(), item.getAmount(), item.getType().name());
		} else {
			send(beingSponsored, "%s has sponsored you %d enchanted %s(s).",
					player.getName(), item.getAmount(), item.getType().name());
		}

		for (ItemStack drop : beingSponsored.getInventory().addItem(item)
				.values()) {
			beingSponsored.getWorld().dropItem(beingSponsored.getLocation(),
					drop);
		}
		if (item.getEnchantments().isEmpty()) {
			send(beingSponsored, "You have sponsored %s %d %s(s) for $%.2f.",
					player.getName(), item.getAmount(), item.getType().name(),
					price);
		} else {
			send(beingSponsored,
					"You have sponsored %s %d enchanted %s(s) for $%.2f.",
					player.getName(), item.getAmount(), item.getType().name(),
					price);
		}

	}

	@EventHandler
	public void playerClickedBlock(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		if(!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if (chestAdders.containsKey(player)) {
			Session session = chestAdders.get(player);
			HungerGame game = session.getGame();
			if (game == null) {
				error(player,
						"%s has been removed recently due to unknown reasons.");
				return;
			}
			Block block = event.getClickedBlock();
			if(action == Action.LEFT_CLICK_BLOCK) {
				if (!(block.getState() instanceof Chest)) {
					error(player, "Block is not a chest.");
					return;
				}
				if (game.addChest(block.getLocation())) {
					send(player, "Chest has been added to %s.", game.getName());
				}

				else {
					error(player, "Chest has already been added to game %s.",
							game.getName());
				}
				session.clicked();
			}
			
			else {
				send(player, "You have added %d chests to the game %s.", session.getBlocks(), game.getName());
				chestAdders.remove(player);
			}

		}

		else if (chestRemovers.containsKey(player)) {
			Session session = chestRemovers.get(player);
			HungerGame game = session.getGame();
			if (game == null) {
				error(player,
						"%s has been removed recently due to unknown reasons.");
				return;
			}
			Block block = event.getClickedBlock();
			if(action == Action.LEFT_CLICK_BLOCK) {
				if (!(block.getState() instanceof Chest)) {
					error(player, "Block is not a chest.");
					return;
				}
				if (game.removeChest(block.getLocation())) {
					send(player, "Chest has been removed from %s.", game.getName());
				}

				else {
					error(player, "%s does not contain this chest.", game.getName());
				}
				session.clicked();
			}
			
			else {
				send(player, "You have removed %d chests from the game %s.", session.getBlocks(), game.getName());
				chestRemovers.remove(player);
			}

		}

		else if (spawnAdders.containsKey(player)) {
			Session session = spawnAdders.get(player);
			HungerGame game = session.getGame();
			if (game == null) {
				error(player,
						"%s has been removed recently due to unknown reasons.");
				return;
			}
			Location loc = event.getClickedBlock().getLocation();
			World world = loc.getWorld();
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY() + 1;
			double z = loc.getBlockZ() + 0.5;
			loc = new Location(world, x, y, z);
			if(action == Action.LEFT_CLICK_BLOCK) {
				if (game.addSpawnPoint(loc)) {
					send(player, "Spawn point has been added to %s.",
							game.getName());
				}

				else {
					error(player, "%s already has this spawn point.",
							game.getName());
				}
				session.clicked();
			}
			
			else {
				send(player, "You have added %d spawn points to the game %s.", session.getBlocks(), game.getName());
				spawnAdders.remove(player);
			}

		}

		else if (spawnRemovers.containsKey(player)) {
			Session session = spawnRemovers.get(player);
			HungerGame game = session.getGame();
			if (game == null) {
				error(player,
						"%s has been removed recently due to unknown reasons.");
				return;
			}
			Location loc = event.getClickedBlock().getLocation();
			World world = loc.getWorld();
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY() + 1;
			double z = loc.getBlockZ() + 0.5;
			loc = new Location(world, x, y, z);
			if(action == Action.LEFT_CLICK_BLOCK) {
				if (game.removeSpawnPoint(loc)) {
					send(player, "Spawn point has been removed from %s.",
							game.getName());
				}

				else {
					error(player, "%s does not contain this spawn point.",
							game.getName());
				}
				session.clicked();
			}
			
			else {
				send(player, "You have removed %d spawn points from the game %s.", session.getBlocks(), game.getName());
				spawnRemovers.remove(player);
			}

		}

	}

	public static boolean hasInventoryBeenCleared(Player player) {//Just in case we want a config option to force clear inv.
		PlayerInventory inventory = player.getInventory();
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}

		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		return true;
	}

	public static void fillChest(Chest chest, List<String> itemsets) {
		if (globalChestLoot.isEmpty() && (itemsets == null || itemsets.isEmpty())) {
			return;
		}
		Inventory inv = chest.getInventory();
		inv.clear();
		int num = 3 + rand.nextInt(8);
		Map<ItemStack, Float> itemMap = Config.getAllChestLootWithGlobal(itemsets);
		List<ItemStack> items = (List<ItemStack>) itemMap.keySet();
		for (int cntr = 0; cntr < num; cntr++) {
			int index = rand.nextInt(inv.getSize());
			if (inv.getItem(index) != null) {
				cntr--;
				continue;
			}
			ItemStack item = items.get(rand.nextInt(items.size()));
			if (itemMap.get(item) >= rand.nextFloat()) {
				inv.setItem(index, item);
			}

		}

	}

	public static Plugin getInstance() {
		return instance;
	}

	public static boolean checkPermission(Player player, Defaults.Perm perm) {
		if (!Plugin.hasPermission(player, perm)) {
			error(player, "You do not have permission.");
			return false;
		}
		return true;
	}
	
	private static class Session {
		private int blocks;
		private String game;
		
		public Session(String game) {
			this.game = game;
			this.blocks = 0;
		}
		
		public HungerGame getGame() {
			return GameManager.getGame(game);
		}
		
		public void clicked() {
			blocks++;
		}
		
		public int getBlocks() {
			return blocks;
		}

	}

}
