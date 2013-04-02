package me.kitskub.hungergames;

import me.kitskub.hungergames.listeners.ActivityListener;
import me.kitskub.hungergames.listeners.CommandListener;
import me.kitskub.hungergames.listeners.LobbyListener;
import me.kitskub.hungergames.listeners.SignListener;
import me.kitskub.hungergames.listeners.EntityListener;
import me.kitskub.hungergames.listeners.ChatListener;
import me.kitskub.hungergames.listeners.PlayerListener;
import me.kitskub.hungergames.listeners.BlockListener;
import me.kitskub.hungergames.listeners.InventoryListener;
import me.kitskub.hungergames.listeners.TeleportListener;
import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.commands.CommandHandler;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.games.PlayerQueueHandler;
import me.kitskub.hungergames.games.TimedGameListener;
import me.kitskub.hungergames.register.BukkitPermission;
import me.kitskub.hungergames.register.Economy;
import me.kitskub.hungergames.register.HGPermission;
import me.kitskub.hungergames.register.VaultPermission;
import me.kitskub.hungergames.reset.ResetHandler;
import me.kitskub.hungergames.stats.TimeListener;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.Item;

import java.io.IOException;
import java.util.Random;
import me.kitskub.hungergames.listeners.SessionListener;

import net.h31ix.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
	private static GameManager gameManager;
	
	@Override
	public void onEnable() {
		instance = this;
		registerCommands();
		ConfigurationSerialization.registerClass(Item.class, "Item");
		Files.loadAll();
		rand = new Random(getName().hashCode());
		registerEvents();
		loadRegistry();
		loadResetter();
		callTasks();
		gameManager = new GameManager();
		gameManager.loadGames();
		LobbyListener.load();
		Logging.info("%s games loaded.", gameManager.getRawGames().size());
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
		for (HungerGame game : gameManager.getRawGames()) {
			game.stopGame(false);
		}
		gameManager.saveGames();
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
		pm.registerEvents(new TimedGameListener(), instance);
		pm.registerEvents(new TimeListener(), instance);
		pm.registerEvents(new LobbyListener(), instance);
		if (Defaults.Config.AUTO_JOIN.getGlobalBoolean()) pm.registerEvents(new PlayerQueueHandler(), instance);
	}
	
	public static void reload() {
		Files.loadAll();
		gameManager.loadGames();
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
	
	public me.kitskub.hungergames.api.GameManager getGameManager() {
		return gameManager;
	}
}