package com.randude14.hungergames;

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
import java.util.Random;

import net.h31ix.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		}, 0L, Defaults.Config.UPDATE_DELAY.getGlobalInt() * 20L * 60L);
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
	    if (Defaults.Config.FORCE_INTERNAL.getGlobalBoolean()) {
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
		if (Defaults.Config.AUTO_JOIN_ALLOWED.getGlobalBoolean()) pm.registerEvents(new PlayerQueueHandler(), instance);
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

	public static Random getRandom() {
		return rand;
	}

	public static void playerLeftServer(Player player) {
		SessionListener.removePlayer(player);
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