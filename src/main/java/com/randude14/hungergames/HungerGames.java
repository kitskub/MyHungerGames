package com.randude14.hungergames;

import com.google.common.base.Strings;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.CommandHandler;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.games.PlayerQueueHandler;
import com.randude14.hungergames.games.TimedGameRunnable;
import com.randude14.hungergames.listeners.*;
import com.randude14.hungergames.register.BukkitPermission;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.HGPermission;
import com.randude14.hungergames.register.VaultPermission;
import com.randude14.hungergames.reset.ResetHandler;
import com.randude14.hungergames.stats.TimeListener;
import com.randude14.hungergames.utils.ChatUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.text.*;

import net.h31ix.updater.Updater;

import org.apache.commons.lang.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerGames extends JavaPlugin{
	public static final String CMD_ADMIN = "hga", CMD_USER = "hg";
	private static HungerGames instance;
	private static HGPermission perm;
	private static Economy econ;
	private static Random rand;
	
	@Override
	public void onEnable() {
		instance = this;
		registerCommands();
		Files.loadAll();
		rand = new Random(getName().hashCode());
		registerEvents();
		updateConfig();
		loadRegistry();
		loadResetter();
		callTasks();
		GameManager.INSTANCE.loadGames();
		LobbyListener.load();
		Logging.info("%s games loaded.", GameManager.INSTANCE.getRawGames().size());
		Bukkit.getScheduler().runTask(this, new Runnable() {
			public void run() {
				try {
				Metrics metrics = new Metrics();
				metrics.beginMeasuringPlugin(HungerGames.getInstance());
				} catch (IOException e) {
				// Fail silently
				}
			}
		});
		Logging.info("Enabled.");
	}

	private void callTasks() {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this,
			new Runnable() {
			public void run() {
				Updater updater = new Updater(HungerGames.getInstance(), "myhungergames", HungerGames.getInstance().getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
				if (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE))
					Logging.warning("There is a new version: %s (You are running %s)", updater.getLatestVersionString(), getDescription().getVersion());
				}
		}, 0L, Config.getUpdateDelay() * 20L * 60L);
	}

	@Override
	public void onDisable() {
		for (HungerGame game : GameManager.INSTANCE.getRawGames()) {
			game.stopGame(false);
		}
		GameManager.INSTANCE.saveGames();
		SignListener.saveSigns();
		Logging.info("Games saved.");
		Files.saveAll();
		Logging.info("Disabled.");
	}

	private static void registerCommands() {
		instance.getCommand(CMD_USER).setExecutor(CommandHandler.getInstance(CMD_USER));
		instance.getCommand(CMD_ADMIN).setExecutor(CommandHandler.getInstance(CMD_ADMIN));
		for (Perm p : Perm.values()) {
			Permission permission = p.getPermission();
			if (p.getParent() != null) {
				permission.addParent(p.getParent().getPermission(), true);
			}
		}
		Commands.init();
	}
	
	private static void loadRegistry() {
	    if (!VaultPermission.isVaultInstalled()) {
		Logging.info("Vault is not installed, defaulting to Bukkit perms.");
		HGPermission.INSTANCE = perm = new BukkitPermission();
		return;
	    } else {
		HGPermission.INSTANCE = perm = new VaultPermission();
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
		    ResetHandler.setRessetter(ResetHandler.Resetters.INTERNAL);
		    return;
	    }
	    if (Bukkit.getPluginManager().getPlugin("HawkEye") != null && Bukkit.getPluginManager().getPlugin("HawkEye").isEnabled()) {
		    Logging.info("Hawkeye is installed, using for resetter.");
		    ResetHandler.setRessetter(ResetHandler.Resetters.HAWKEYE);
	    } else if (Bukkit.getPluginManager().getPlugin("LogBlock") != null && Bukkit.getPluginManager().getPlugin("LogBlock").isEnabled()) {
		    Logging.info("LogBlock is installed, using for resetter.");
		    ResetHandler.setRessetter(ResetHandler.Resetters.LOGBLOCK);
	    } else if (Bukkit.getPluginManager().getPlugin("Multiverse-Adventure") != null && Bukkit.getPluginManager().getPlugin("Multiverse-Adventure").isEnabled()) {
		    Logging.info("LogBlock is installed, using for resetter.");
		    ResetHandler.setRessetter(ResetHandler.Resetters.MVA);
	    } else {
		    Logging.info("No logging plugins installed, using internal resetter.");
		    ResetHandler.setRessetter(ResetHandler.Resetters.INTERNAL);
	    }
	}

	private static void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ActivityListener(), instance);
		pm.registerEvents(new BlockListener(), instance);
		pm.registerEvents(new CommandListener(), instance);
		pm.registerEvents(new PlayerListener(), instance);
		pm.registerEvents(new EntityListener(), instance);
		pm.registerEvents(new SignListener(), instance);
		pm.registerEvents(new InventoryListener(), instance);
		pm.registerEvents(new SessionListener(), instance);
		pm.registerEvents(new ChatListener(), instance);
		pm.registerEvents(new TeleportListener(), instance);
		pm.registerEvents(new TimedGameRunnable(), instance);
		pm.registerEvents(new TimeListener(), instance);
		pm.registerEvents(new LobbyListener(), instance);
		if (Config.getAutoJoin()) pm.registerEvents(new PlayerQueueHandler(), instance);
	}
	
	private static void updateConfig() {
		if (Files.CONFIG.getConfig().contains("global.chest-loot")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("global.chest-loot").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("global.chest-loot." + key);
				Files.ITEMCONFIG.getConfig().set("global.chest-loot." + key, value);
				Files.CONFIG.getConfig().set("global.chest-loot." + key, null);
			}
		}
		if (Files.CONFIG.getConfig().contains("global.sponsor-loot")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("global.sponsor-loot").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("global.sponsor-loot." + key);
				Files.ITEMCONFIG.getConfig().set("global.sponsor-loot." + key, value);
				Files.CONFIG.getConfig().set("global.sponsor-loot." + key, null);
			}
		}
		if (Files.CONFIG.getConfig().contains("itemsets")) {
			for (String key : Files.CONFIG.getConfig().getConfigurationSection("itemsets").getKeys(false)) {
				Object value = Files.CONFIG.getConfig().get("itemsets." + key);
				Files.ITEMCONFIG.getConfig().set("itemsets." + key, value);
				Files.CONFIG.getConfig().set("itemsets." + key, null);
			}
		}
	}
	
	public static void reload() {
		Files.loadAll();
		GameManager.INSTANCE.loadGames();
		SignListener.loadSigns();
		loadRegistry();
	}

	public static boolean hasPermission(CommandSender cs, Defaults.Perm perm) {
		return HungerGames.perm.hasPermission(cs, perm);
	}

	public static boolean equals(Location loc1, Location loc2) {
		return loc1.getWorld() == loc2.getWorld()
			&& loc1.getBlockX() == loc2.getBlockX()
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

	public static int scheduleTask(Runnable runnable, long initial, long delay) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, runnable, initial, delay);
	}

	public static void cancelTask(int taskID) {
		Bukkit.getServer().getScheduler().cancelTask(taskID);
	}

	public static void callEvent(Event event) {
		instance.getServer().getPluginManager().callEvent(event);
	}

	public static String parseToString(Location loc) {
		if (loc == null) return "";
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(symbols);
		df.setGroupingUsed(false);
		return String.format("%s %s %s %s %s %s", df.format((Number) loc.getX()), df.format((Number) loc.getY()), df.format((Number) loc.getZ()), df.format((Number) loc.getYaw()), 
			df.format((Number) loc.getPitch()), loc.getWorld().getName());
	}

	public static Location parseToLoc(String str) throws NumberFormatException, WorldNotFoundException, IllegalArgumentException {
		Strings.emptyToNull(str);
		if (str == null) {
			throw new IllegalArgumentException("Location can not be null.");
		}
		String[] strs = str.split(" ");
		double x = Double.parseDouble(strs[0]);
		double y = Double.parseDouble(strs[1]);
		double z = Double.parseDouble(strs[2]);
		float yaw = Float.parseFloat(strs[3]);
		float pitch = Float.parseFloat(strs[4]);
		World world = Bukkit.getServer().getWorld(strs[5]);
		if (world == null) throw new WorldNotFoundException("Could not load world \"" + strs[5] + "\" when loading location \"" + str);
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static String formatTime(int time) {

		List<String> strs = new ArrayList<String>();
		if(time > 3600) {
			strs.add(String.format("%d hour(s)", (time / 3600) % 24));
		}
		if(time > 60) {
			strs.add(String.format("%d minute(s)", (time / 60) % 60));
		}
		strs.add(String.format("%d second(s)", time % 60));
		StringBuilder buff = new StringBuilder();
		String sep = "";
		for (String str : strs) {
			buff.append(sep);
			buff.append(str);
			sep = ", ";
		}
		return buff.toString();
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
		for (ItemStack item : inventory.getArmorContents()) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}

		}
		return true;
	}

	public static void fillFixedChest(Chest chest, String name) {
		chest.getInventory().clear();
		List<ItemStack> items = ItemConfig.getFixedChest(name);
		for (ItemStack stack : items) {
			int index = 0;
			do {
				index = rand.nextInt(chest.getInventory().getSize());
			} while (chest.getInventory().getItem(index) != null);
			
			chest.getInventory().setItem(index, stack);
		}
	}
	
	public static void fillChest(Chest chest, float weight, List<String> itemsets) {
		if (ItemConfig.getGlobalChestLoot().isEmpty() && (itemsets == null || itemsets.isEmpty())) {
			return;
		}

		chest.getInventory().clear();
		Map<ItemStack, Float> itemMap = ItemConfig.getAllChestLootWithGlobal(itemsets);
		List<ItemStack> items = new ArrayList<ItemStack>(itemMap.keySet());
		int size = chest.getInventory().getSize();
		final int maxItemSize = 100;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		int minItems = (int) Math.floor(numItems/2);
		int itemsIn = 0;
		for (int cntr = 0; cntr < numItems || itemsIn < minItems; cntr++) {
			int index = 0;
			do {
				index = rand.nextInt(chest.getInventory().getSize());
			} while (chest.getInventory().getItem(index) != null);
			
			ItemStack item = items.get(rand.nextInt(items.size()));
			if (weight * itemMap.get(item) >= rand.nextFloat()) {
				chest.getInventory().setItem(index, item);
				itemsIn++;
			}

		}
	}

	public static void rewardPlayer(Player player) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		items.addAll(ItemConfig.getStaticRewards());
		Logging.debug("rewardPlayer: items after static: " + ArrayUtils.toString(items));
		Map<ItemStack, Float> itemMap = ItemConfig.getRandomRewards();

		int size = ItemConfig.getMaxRandomItems();
		final int maxItemSize = 25;
		int numItems = items.size() >= maxItemSize ? size : (int) Math.ceil((size * Math.sqrt(items.size()))/Math.sqrt(maxItemSize));
		Logging.debug("rewardPlayer: items after random: " + ArrayUtils.toString(items));
		for (int cntr = 0; cntr < numItems; cntr++) {			
			ItemStack item = null;
			while (item == null) { // TODO items should not have any null elements, but do.
				item = items.get(rand.nextInt(items.size()));
			}
			if (itemMap.get(item) >= rand.nextFloat()) {
				items.add(item);
			}

		}
		for (ItemStack i : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
			player.getLocation().getWorld().dropItem(player.getLocation(), i);
		}
	}
	
	public static HungerGames getInstance() {
		return instance;
	}

	public static boolean checkPermission(CommandSender cs, Defaults.Perm perm) {
		if (!HungerGames.hasPermission(cs, perm)) {
			cs.sendMessage(ChatColor.RED + Lang.getNoPerm());
			return false;
		}
		return true;
	}
}