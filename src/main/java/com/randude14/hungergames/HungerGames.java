package com.randude14.hungergames;

import com.randude14.hungergames.commands.CommandHandler;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
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
	
	@Override
	public void onEnable() {
		instance = this;
		CommandHandler commands = new CommandHandler();
		getCommand(CMD_USER).setExecutor(commands);
		getCommand(CMD_ADMIN).setExecutor(commands);
		rand = new Random(getName().hashCode());
		manager = new GameManager();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new CommandListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new EntityListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new SessionListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new TeleportListener(), this);
		if (!new File(getDataFolder(), "config.yml").exists()) {
		    Logging.info("config.yml not found. Saving defaults.");
		    saveDefaultConfig();
		}
		loadRegistry();
		loadResetter();
		callTasks();
		GameManager.loadGames();
		Logging.info("Games loaded.");
		try {
		    Metrics metrics = new Metrics();
		    metrics.beginMeasuringPlugin(this);
		} catch (IOException e) {
		// Fail silently
		}
		Logging.info("Enabled.");
	}

	private void callTasks() {
	    HungerGames.scheduleTask(new Runnable() {
		public void run() {
		    String installedVersion = getDescription().getVersion();
		    String checkVersion = latestVersion();
		    if (!checkVersion.equalsIgnoreCase(installedVersion))
			    Logging.warning("There is a new version: %s (You are running %s)", checkVersion, installedVersion);
		}
	    }, 0L, Config.getUpdateDelay() * 20L * 60L);
	}

	@Override
	public void onDisable() {
	    GameManager.saveGames();
	    Logging.info("Games saved.");
	    Logging.info("Disabled.");
	}

	private static void loadRegistry() {
	    if (!VaultPermission.isVaultInstalled()) {
		Logging.info("Vault is not installed, defaulting to Bukkit perms.");
		perm = new BukkitPermission();
		return;
	    } else {
		perm = new VaultPermission();
	    }

	    if (!Economy.isVaultInstalled()) {
		Logging.warning("Vault is not installed, economy use disabled.");
		econ = null;
	    } else {
		econ = new Economy();
	    }
	}
	
	private static void loadResetter() {
	    if (Config.getForceInternalGlobal()) {
		    Logging.info("Forcing internal resetter.");
		    ResetHandler.setRessetter(ResetHandler.INTERNAL);
		    return;
	    }
	    if (Bukkit.getPluginManager().getPlugin("HawkEye") != null && Bukkit.getPluginManager().getPlugin("HawkEye").isEnabled()) {
		    Logging.info("Hawkeye is installed, using for resetter.");
		    ResetHandler.setRessetter(ResetHandler.HAWKEYE);
		    return;
	    } else if (Bukkit.getPluginManager().getPlugin("LogBlock") != null && Bukkit.getPluginManager().getPlugin("LogBlock").isEnabled()){
		    Logging.info("LogBlock is installed, using for resetter.");
		    ResetHandler.setRessetter(ResetHandler.LOGBLOCK);
		    return;
	    } else {
		    Logging.info("No logging plugins installed, using internal resetter.");
		    ResetHandler.setRessetter(ResetHandler.INTERNAL);
		    return;
	    }
	}

	public static void reload() {
	    instance.reloadConfig();
	    GameManager.loadGames();
	    loadRegistry();
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

	public static Location parseToLoc(String str) throws NumberFormatException{
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

	public static void playerLeftServer(Player player) {
		SessionListener.removePlayer(player);
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
		if (Config.getGlobalChestLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
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