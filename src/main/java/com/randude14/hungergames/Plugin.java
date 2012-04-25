package com.randude14.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.randude14.hungergames.games.HungerGame;
import org.bukkit.Bukkit;

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
	private static Map<Player, String> chestAdders;
	private static Map<Player, String> chestRemovers;
	private static Map<Player, String> spawnAdders;
	private static Map<Player, String> spawnRemovers;
	private static Map<Player, String> sponsors;
	private static Map<ItemStack, Float> chestLoot;
	private static Map<ItemStack, Double> sponsorLoot;

	@Override
	public void onEnable() {
		instance = this;
		Commands commands = new Commands(this);
		getCommand(CMD_USER).setExecutor(commands);
		getCommand(CMD_ADMIN).setExecutor(commands);
		rand = new Random(getName().hashCode());
		manager = new GameManager();
		frozenPlayers = new HashMap<Player, Location>();
		chestAdders = new HashMap<Player, String>();
		chestRemovers = new HashMap<Player, String>();
		spawnAdders = new HashMap<Player, String>();
		spawnRemovers = new HashMap<Player, String>();
		sponsors = new HashMap<Player, String>();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(manager, this);
		chestLoot = Config.getChestLoot();
		sponsorLoot = Config.getSponsorLoot();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			info("config not found. saving defaults.");
			saveDefaultConfig();
		}
		setupPermission();
		if (!setupEconomy()) {
			info("Economy was not found, shutting down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		GameManager.loadGames();
		info("games loaded.");
		info("enabled.");
	}

	@Override
	public void onDisable() {
		GameManager.saveGames();
		info("games saved.");
		info("disabled.");
	}

	public void reload() {
		reloadConfig();
		chestLoot = Config.getChestLoot();
		sponsorLoot = Config.getSponsorLoot();
		GameManager.loadGames();
		for (Player player : sponsors.keySet()) {
			error(player,
					"The items available for sponsoring have recently changed. Here are the new items...");
			addSponsor(player, sponsors.get(player));
		}

	}

	private static void setupPermission() {
		RegisteredServiceProvider<Permission> provider = Bukkit.getServer()
				.getServicesManager().getRegistration(Permission.class);
		perm = provider.getProvider();
	}

	private static boolean setupEconomy() {
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (provider == null) {
			return false;
		}
		econ = provider.getProvider();
		return econ != null;
	}

	public static void info(String mess) {
		logger.log(Level.INFO, getLogPrefix() + mess);
	}

	public static void warning(String mess) {
		logger.log(Level.WARNING, getLogPrefix() + mess);
	}

	public static void severe(String mess) {
		logger.log(Level.SEVERE, getLogPrefix() + mess);
	}

	public static String getLogPrefix() {
		return String.format("[%s] v%s - ", instance.getName(), instance.getDescription()
				.getVersion());
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

	public static void broadcast(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + getPrefix() + message);
		}

		message = ChatColor.stripColor(message);
		info(message);
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

	public static void send(Player player, ChatColor color, String mess) {
		player.sendMessage(color + mess);
	}

	public static void send(Player player, String mess) {
		player.sendMessage(ChatColor.GRAY + mess);
	}

	public static void help(Player player, String mess) {
		player.sendMessage(ChatColor.GOLD + mess);
	}

	public static void error(Player player, String mess) {
		player.sendMessage(ChatColor.RED + mess);
	}

	public static boolean hasPermission(Player p, String permission) {
		String world = p.getWorld().getName();
		String player = p.getName();
		return perm.has(world, player, permission);
	}

	public static boolean equals(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
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
		chestAdders.put(player, name);
	}

	public static void addChestRemover(Player player, String name) {
		chestRemovers.put(player, name);
	}

	public static void addSpawnAdder(Player player, String name) {
		spawnAdders.put(player, name);
	}

	public static void addSpawnRemover(Player player, String name) {
		spawnRemovers.put(player, name);
	}

	public static boolean addSponsor(Player player, String playerToBeSponsored) {
		if (sponsorLoot.isEmpty()) {
			error(player, "No items are available to sponsor.");
			return false;
		}

		else {
			sponsors.put(player, playerToBeSponsored);
			send(player, ChatColor.GREEN, getHeadLiner());
			send(player,
					ChatColor.YELLOW,
					String.format("Type the number next to the item you would like sponsor to %s.", playerToBeSponsored));
			send(player, "");
			int num = 1;
			for (ItemStack item : sponsorLoot.keySet()) {
				String mess = String.format(">> %d - %s: %d", num, item.getType()
						.name(), item.getAmount());
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
		if (!frozenPlayers.containsKey(player)) {
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
			error(player, String.format("'%s' is not an integer.", mess));
			return;
		}

		int size = sponsorLoot.size();
		if (choice < 0 || choice >= size) {
			error(player, String.format("Choice '%d' does not exist."));
			return;
		}
		Player beingSponsored = getServer().getPlayer(sponsor);
		if (beingSponsored == null) {
			error(player, String.format("'%s' is not online anymore.", sponsor));
			return;
		}
		HungerGame game = GameManager.getSession(player);
		if (game == null) {
			error(player,
					String.format("'%s' is no longer in a game.", sponsor));
			return;
		}
		ItemStack item = new ArrayList<ItemStack>(sponsorLoot.keySet())
				.get(choice);
		double price = sponsorLoot.get(item);
		String name = beingSponsored.getName();
		if (!econ.has(name, price)) {
			error(player, String.format("You do not have enough money."));
			return;
		}
		econ.withdrawPlayer(name, price);
		if (item.getEnchantments().isEmpty()) {
			send(beingSponsored, String.format(
					"%s has sponsored you %d %s(s).", player.getName(),
					item.getAmount(), item.getType().name()));
		}
		else {
			send(beingSponsored, String.format(
					"%s has sponsored you %d enchanted %s(s).", player.getName(),
					item.getAmount(), item.getType().name()));
		}
		
		for (ItemStack drop : beingSponsored.getInventory().addItem(item)
				.values()) {
			beingSponsored.getWorld().dropItem(beingSponsored.getLocation(),
					drop);
		}
		if (item.getEnchantments().isEmpty()) {
			send(beingSponsored, String.format(
					"You have sponsored %s %d %s(s) for $%.2f.", player.getName(),
					item.getAmount(), item.getType().name(), price));
		}
		else {
			send(beingSponsored, String.format(
					"You have sponsored %s %d enchanted %s(s) for $%.2f.", player.getName(),
					item.getAmount(), item.getType().name(), price));
		}
		
	}

	@EventHandler
	public void playerHitBlock(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();

		if (chestAdders.containsKey(player)) {
			String name = chestAdders.remove(player);
			HungerGame game = GameManager.getGame(name);
			if (game == null) {
				error(player,
						String.format("%s has been removed recently due to unknown reasons."));
				return;
			}
			Block block = event.getBlock();
			if (!(block.getState() instanceof Chest)) {
				error(player, "Block is not a chest.");
				return;
			}
			if (game.addChest(block.getLocation())) {
				send(player,
						String.format("Chest has been added to %s.",
								game.getName()));
			}

			else {
				error(player, String.format(
						"Chest has already been added to game %s.",
						game.getName()));
			}

		}

		else if (chestRemovers.containsKey(player)) {
			String name = chestRemovers.remove(player);
			HungerGame game = GameManager.getGame(name);
			if (game == null) {
				error(player,
						String.format("%s has been removed recently due to unknown reasons."));
				return;
			}
			Block block = event.getBlock();
			if (!(block.getState() instanceof Chest)) {
				error(player, "Block is not a chest.");
				return;
			}
			if (game.removeChest(block.getLocation())) {
				send(player,
						String.format("Chest has been removed from %s.",
								game.getName()));
			}

			else {
				error(player,
						String.format("%s does not contain this chest.",
								game.getName()));
			}

		}

		else if (spawnAdders.containsKey(player)) {
			String name = spawnAdders.remove(player);
			HungerGame game = GameManager.getGame(name);
			if (game == null) {
				error(player,
						String.format("%s has been removed recently due to unknown reasons."));
				return;
			}
			Location loc = event.getBlock().getLocation();
			World world = loc.getWorld();
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY() + 1;
			double z = loc.getBlockZ() + 0.5;
			loc = new Location(world, x, y, z);
			if (game.addSpawnPoint(loc)) {
				send(player,
						String.format("Spawn point has been added to %s.",
								game.getName()));
			}

			else {
				error(player,
						String.format("%s already has this spawn point.",
								game.getName()));
			}

		}

		else if (spawnRemovers.containsKey(player)) {
			String name = spawnRemovers.remove(player);
			HungerGame game = GameManager.getGame(name);
			if (game == null) {
				error(player,
						String.format("%s has been removed recently due to unknown reasons."));
				return;
			}
			Location loc = event.getBlock().getLocation();
			World world = loc.getWorld();
			double x = loc.getBlockX() + 0.5;
			double y = loc.getBlockY() + 1;
			double z = loc.getBlockZ() + 0.5;
			loc = new Location(world, x, y, z);
			if (game.removeSpawnPoint(loc)) {
				send(player,
						String.format("Spawn point has been removed from %s.",
								game.getName()));
			}

			else {
				error(player,
						String.format("%s does not contain this spawn point.",
								game.getName()));
			}

		}

	}

	public static void giveMultiversePermission(Player player) {
		perm.playerAdd(player, "multiverse.access.survival");
	}

	public static void takeMultiversePermission(Player player) {
		perm.playerRemove(player, "multiverse.access.survival");
	}

	public static boolean hasInventoryBeenCleared(Player player) {
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

	public static void fillChest(Chest chest) {
		if (chestLoot.isEmpty()) {
			return;
		}
		Inventory inv = chest.getInventory();
		inv.clear();
		int num = 3 + rand.nextInt(8);
		List<ItemStack> items = new ArrayList<ItemStack>(chestLoot.keySet());
		for (int cntr = 0; cntr < num; cntr++) {
			int index = rand.nextInt(inv.getSize());
			ItemStack item = items.get(rand.nextInt(items.size()));
			if (chestLoot.get(item) >= rand.nextFloat()) {
				inv.setItem(index, item);
			}

		}

	}

	public static Plugin getInstance() {
		return instance;
	}

}
