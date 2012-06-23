package com.randude14.hungergames;

import com.randude14.hungergames.commands.CommandHandler;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.listeners.*;
import com.randude14.hungergames.register.BukkitPermission;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.Permission;
import com.randude14.hungergames.register.VaultPermission;
import com.randude14.hungergames.reset.ResetHandler;

import com.randude14.hungergames.utils.ChatUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerGames extends JavaPlugin{
	public static final String CMD_ADMIN = "hga";
	public static final String CMD_USER = "hg";
	private static HungerGames instance;
	private static Permission perm;
	private static Economy econ;
	private static GameManager manager;
	private static Random rand;
	private static Map<String, Location> frozenPlayers;
	private static Map<String, String> sponsors; // <sponsor, sponsee>
	private static Map<String, String> spectators; // <player, game>
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
		frozenPlayers = new HashMap<String, Location>();
		sponsors = new HashMap<String, String>();
		spectators = new HashMap<String, String>();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new CommandListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new SessionListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new TeleportListener(), this);
		if (!new File(getDataFolder(), "config.yml").exists()) {
		    ChatUtils.info("config.yml not found. Saving defaults.");
		    saveDefaultConfig();
		}
		globalChestLoot = Config.getGlobalChestLoot();
		globalSponsorLoot = Config.getGlobalSponsorLoot();
		chestLoots = new HashMap<String, Map<ItemStack, Float>>();
		sponsorLoots = new HashMap<String, Map<ItemStack, Double>>();
		for (String itemset : Config.getItemSets()) {
		    chestLoots.put(itemset, Config.getChestLoot(itemset));
		    sponsorLoots.put(itemset, Config.getSponsorLoot(itemset));
		}
		loadRegistry();
		loadResetter();
		callTasks();
		GameManager.loadGames();
		ChatUtils.info("Games loaded.");
		try {
		    Metrics metrics = new Metrics();
		    metrics.beginMeasuringPlugin(this);
		} catch (IOException e) {
		// Fail silently
		}
		ChatUtils.info("Enabled.");
	}

	private void callTasks() {
	    HungerGames.scheduleTask(new Runnable() {
		public void run() {
		    String installedVersion = getDescription().getVersion();
		    String checkVersion = latestVersion();
		    if (!checkVersion.equalsIgnoreCase(installedVersion))
			    ChatUtils.warning("There is a new version: %s (You are running %s)",
				    checkVersion, installedVersion);
		}
	    }, 0L, Config.getUpdateDelay() * 20L * 60L);
	}

	@Override
	public void onDisable() {
	    GameManager.saveGames();
	    ChatUtils.info("Games saved.");
	    ChatUtils.info("Disabled.");
	}

	private static void loadRegistry() {
	    if (!VaultPermission.isVaultInstalled()) {
		ChatUtils.info("Vault is not installed, defaulting to Bukkit perms.");
		perm = new BukkitPermission();
		return;
	    } else {
		perm = new VaultPermission();
	    }

	    if (!Economy.isVaultInstalled()) {
		ChatUtils.warning("Vault is not installed, economy use disabled.");
		econ = null;
	    } else {
		econ = new Economy();
	    }
	}
	
	private static void loadResetter() { // TODO finish implementation
	    /*if (Bukkit.getPluginManager().getPlugin("HawkEye") != null && Bukkit.getPluginManager().getPlugin("HawkEye").isEnabled()) {
		ChatUtils.info("Hawkeye is installed, using for resetter.");
		ResetHandler.setRessetter(ResetHandler.HAWKEYE);
		return;
	    } else */if (Bukkit.getPluginManager().getPlugin("LogBlock") != null && Bukkit.getPluginManager().getPlugin("LogBlock").isEnabled()){
		ChatUtils.info("LogBlock is installed, using for resetter.");
		ResetHandler.setRessetter(ResetHandler.LOGBLOCK);
		return;
	    } else {
		ChatUtils.info("No logging plugins installed, using internal resetter.");
		ResetHandler.setRessetter(ResetHandler.INTERNAL);
		return;
	    }
	}

	public static void reload() {
	    instance.reloadConfig();
	    globalChestLoot = Config.getGlobalChestLoot();
	    globalSponsorLoot = Config.getGlobalSponsorLoot();
	    chestLoots = new HashMap<String, Map<ItemStack, Float>>();
	    sponsorLoots = new HashMap<String, Map<ItemStack, Double>>();
	    for (String itemset : Config.getItemSets()) {
		chestLoots.put(itemset, Config.getChestLoot(itemset));
		sponsorLoots.put(itemset, Config.getSponsorLoot(itemset));
	    }
	    GameManager.loadGames();
	    loadRegistry();
	    for (String playerName : sponsors.keySet()) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) continue;
		ChatUtils.error(player, "The items available for sponsoring have recently changed. Here are the new items...");
		addSponsor(player, sponsors.remove(playerName));
	    }
	}

	public static boolean hasPermission(Player player, Defaults.Perm perm) {
		return HungerGames.perm.hasPermission(player, perm);
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
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
			return;
		}
		econ.withdraw(player.getName(), amount);
	}

	public static void deposit(Player player, double amount) {
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
			return;
		}
		econ.deposit(player.getName(), amount);
	}

	public static boolean hasEnough(Player player, double amount) {
		if (!isEconomyEnabled()) {
			ChatUtils.error(player, "Economy use has been disabled.");
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
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, runnable, initial, delay);
	}

	public static void cancelTask(int taskID) {
		Bukkit.getServer().getScheduler().cancelTask(taskID);
	}

	public static void freezePlayer(Player player) {
		frozenPlayers.put(player.getName(), player.getLocation());
	}

	public static void unfreezePlayer(Player player) {
		frozenPlayers.remove(player.getName());
	}

	public static boolean isPlayerFrozen(Player player) {
		return frozenPlayers.containsKey(player.getName());
	}

	public static void addChestAdder(Player player, String name) {
		SessionListener.addChestAdder(player, name);
	}

	public static void addChestRemover(Player player, String name) {
		SessionListener.addChestRemover(player, name);
	}

	public static void addSpawnAdder(Player player, String name) {
		SessionListener.addSpawnAdder(player, name);
	}

	public static void addSpawnRemover(Player player, String name) {
		SessionListener.addSpawnRemover(player, name);
	}

	public static boolean addSponsor(Player player, String playerToBeSponsored) {
	    Player sponsoredPlayer = Bukkit.getPlayer(playerToBeSponsored);
	    HungerGame game = GameManager.getSession(sponsoredPlayer);
	    if (game == null || !game.getPlayerStat(player).isPlaying()) {
		    ChatUtils.error(player, "That player is playing in a game.");
		    return false;
	    }
	    List<String> itemsets = game.getItemSets();
	    if (globalSponsorLoot.isEmpty() && (itemsets == null || itemsets.isEmpty())) {
		    ChatUtils.error(player, "No items are available to sponsor.");
		    return false;
	    }

	    if (!isEconomyEnabled()) {
		    ChatUtils.error(player, "Economy use has been disabled.");
		    return false;
	    }
	    sponsors.put(player.getName(), playerToBeSponsored);
	    ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
	    ChatUtils.send(player, ChatColor.YELLOW, "Type the number next to the item you would like sponsor to %s.",
		    playerToBeSponsored);
	    ChatUtils.send(player, "");
	    int num = 1;
	    Map<ItemStack, Double> itemMap = Config.getAllSponsorLootWithGlobal(itemsets);
	    for (ItemStack item : itemMap.keySet()) {
		    String mess = String.format(">> %d - %s: %d", num, item.getType()
				    .name(), item.getAmount());
		    Set<Enchantment> enchants = item.getEnchantments().keySet();
		    int cntr = 0;
		    if (!enchants.isEmpty()) {
			    mess += ", ";
		    }
		    for (Enchantment enchant : enchants) {
			    mess += String.format("%s: %d", enchant.getName(), item.getEnchantmentLevel(enchant));
			    if (cntr < enchants.size() - 1) {
				    mess += ", ";
			    }
			    cntr++;
		    }
		    ChatUtils.send(player, ChatColor.GOLD, mess);
		    num++;
	    }
	    return true;
	}

	public static Map<String, String> getSponsors() {
		return Collections.unmodifiableMap(sponsors);
	}
	
	public static String removeSponsor(Player player) {
		return sponsors.remove(player.getName());
	}
	
	public static void addSpectator(Player player, String gameName) {
		spectators.put(player.getName(), gameName);
	}
	
	public static String getSpectating(Player player) {
	    if (player == null) return "";    
	    if (!spectators.containsKey(player.getName())) return "";
	    return spectators.get(player.getName());
	}
	
	public static String removeSpectator(Player player) {
		return spectators.remove(player.getName());
	}

	public String latestVersion() {
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/myhungergames/files.rss");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
		}
		return getDescription().getVersion();
	}

	public static void callEvent(Event event) {
		instance.getServer().getPluginManager().callEvent(event);
	}

	public static String parseToString(Location loc) {
		return String.format("%.2f %.2f %.2f %.2f %.2f %s", loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 
			loc.getPitch(), loc.getWorld().getName());
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
	
	public static String formatTime(int time) {
		String format = "";
		if(time > 3600) {
			format += String.format("%d hour(s), ", (time / 3600) % 24);
		}
		if(time > 60) {
			format += String.format("%d minute(s), ", (time / 60) % 60);
		}
		format += String.format("%d second(s), ", time % 60);
		return format;
	}

	public static Location getFrozenLocation(Player player) {
		if (!frozenPlayers.containsKey(player.getName())) return null;
		return frozenPlayers.get(player.getName());
	}

	public static void playerLeftServer(Player player) {
		SessionListener.removePlayer(player);
		sponsors.remove(player.getName());
	}

	public static boolean hasInventoryBeenCleared(Player player) {
		PlayerInventory inventory = player.getInventory();
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		/* TODO: this should be included above. Be on the lookout for bug reports
		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		*/
		return true;
	}

	public static void fillInventory(Inventory inv, List<String> itemsets) {
		if (globalChestLoot.isEmpty()
				&& (itemsets == null || itemsets.isEmpty())) {
			return;
		}
		
		inv.clear();
		int num = 3 + rand.nextInt(8);
		Map<ItemStack, Float> itemMap = Config
				.getAllChestLootWithGlobal(itemsets);
		List<ItemStack> items = new ArrayList<ItemStack>(itemMap.keySet());
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

	public static HungerGames getInstance() {
		return instance;
	}

	public static boolean checkPermission(Player player, Defaults.Perm perm) {
		if (!HungerGames.hasPermission(player, perm)) {
			ChatUtils.error(player, "You do not have permission.");
			return false;
		}
		return true;
	}
}
