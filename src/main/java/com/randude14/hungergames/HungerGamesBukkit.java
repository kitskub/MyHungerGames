package com.randude14.hungergames;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.CommandHandler;
import com.randude14.hungergames.core.ServerInterface;
import com.randude14.hungergames.core.bukkit.BukkitServerInterface;
import com.randude14.hungergames.games.PlayerQueueHandler;
import com.randude14.hungergames.games.TimedGameRunnable;
import com.randude14.hungergames.listeners.*;
import com.randude14.hungergames.register.BukkitPermission;
import com.randude14.hungergames.register.Economy;
import com.randude14.hungergames.register.HGPermission;
import com.randude14.hungergames.register.VaultPermission;
import com.randude14.hungergames.reset.ResetHandler;
import com.randude14.hungergames.stats.TimeListener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.net.URL;
import java.text.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerGamesBukkit extends JavaPlugin implements HungerGamesPlugin {
	public static final String CMD_ADMIN = "hga", CMD_USER = "hg";
	private static HungerGamesBukkit instance;
	private static Random rand;

	@Override
	public ServerInterface getServerInterface() {
		return new BukkitServerInterface(getServer());
	}
	
	@Override
	public String getVersion() {
		return instance.getVersion();
	}
	
	@Override
	public void onEnable() {
		HungerGames.enable(this);
	}

	@Override
	public void onDisable() {
		HungerGames.disable();
	}

	@Override
	public void loadMetrics() {
		try {
		    BukkitMetrics metrics = new BukkitMetrics(this);
		    metrics.start();
		} catch (IOException e) {
		// Fail silently
		}
	}

	@Override
	public void registerCommands() {
		instance.getCommand(CMD_USER).setExecutor(CommandHandler.INSTANCE);
		instance.getCommand(CMD_ADMIN).setExecutor(CommandHandler.INSTANCE);
		for (Perm p : Perm.values()) {
			Permission permission = p.getPermission();
			if (p.getParent() != null) {
				permission.addParent(p.getParent().getPermission(), true);
			}
		}
		Commands.init();
	}
	
	@Override
	public Economy loadEconomy() {
	    if (!Economy.isVaultInstalled()) {
		Logging.warning("Vault is not installed, economy use disabled.");
		return null;
	    } else {
		return new Economy();
	    }
	}
	
	@Override
	public HGPermission loadPermission() {
	    if (!VaultPermission.isVaultInstalled()) {
		Logging.info("Vault is not installed, defaulting to Bukkit perms.");
		return new BukkitPermission();
	    } else {
		return new VaultPermission();
	    }
	}
	
	@Override
	public void loadResetter() {
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

	@Override
	public void registerEvents() {
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

	public boolean latestVersionCheck(){
		String datePub = null;
		long timeMod = 0;
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/myhungergames/files.rss");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("pubDate");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				datePub = firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
		}
		DateFormat pubDate = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
		try {
			File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			timeMod = jarFile.lastModified();
		} catch (URISyntaxException e1) {
		}
		try {
			return pubDate.parse(datePub).getTime() <= (timeMod + 86400000);
		} catch (ParseException parseException) {
			return false;
		}
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
	
	public static HungerGamesBukkit getInstance() {
		return instance;
	}
}