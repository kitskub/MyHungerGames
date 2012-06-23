package com.randude14.hungergames;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class Logging {
	private static final Logger logger = Logger.getLogger("MyHungerGames");

	public static void log(Level level, String record) {
		logger.log(level, record);		
	}

	public static void log(Level level, String record, String... strings) {
		logger.log(level, record, strings);		
	}

	static {
		try {
			HungerGames instance = HungerGames.getInstance();
			instance.getDataFolder().mkdirs();
			File file = new File(instance.getDataFolder(), "myhungergames.log");
			if (!file.exists()) file.createNewFile();
			FileHandler handler = new FileHandler("plugins/HungerGames/myhungergames.log", true);
			logger.addHandler(handler);
			logger.setLevel(Level.FINEST);
			Logger parent = Logger.getLogger("Minecraft");
			logger.setParent(parent);
		} catch (IOException ex) {
		}

	}

	public static String getLogPrefix() {
		return String.format("[%s] %s - ", HungerGames.getInstance().getName(), HungerGames.getInstance().getDescription().getVersion());
	}

	public static void info(String format, Object... args) {
		log(Level.INFO, getLogPrefix() + String.format(format, args));
	}

	public static void info(String mess) {
		log(Level.INFO, getLogPrefix() + mess);
	}

	public static void warning(String format, Object... args) {
		log(Level.WARNING, getLogPrefix() + String.format(format, args));
	}

	public static void warning(String mess) {
		log(Level.WARNING, getLogPrefix() + mess);
	}

	public static void severe(String format, Object... args) {
		log(Level.SEVERE, getLogPrefix() + String.format(format, args));
	}

	public static void severe(String mess) {
		log(Level.SEVERE, getLogPrefix() + mess);
	}

	public static void debug(String mess, Object... args) {
		log(Level.FINEST, getLogPrefix() + String.format(mess, args));
	}

	public static void debug(String mess) {
		log(Level.FINEST, getLogPrefix() + mess);
	}
	
	public static class LogCommandSender implements CommandSender, Permissible {
		String who = "";
		
		public LogCommandSender(String who) {
			this.who = who;
		}
		
		public void sendMessage(String string) {
			log(Level.INFO, "CS for " + who + ": " + string);
		}

		public void sendMessage(String[] strings) {
			for (String string : strings) {
				log(Level.INFO, "CS for " + who + ": " + string);
			}
		}

		public Server getServer() {
			return HungerGames.getInstance().getServer();
		}

		public String getName() {
			return "MyHungerGames Logger for " + who;
		}

		public boolean isPermissionSet(String string) {
			return true;
		}

		public boolean isPermissionSet(Permission prmsn) {
			return true;
		}

		public boolean hasPermission(String string) {
			return true;
		}

		public boolean hasPermission(Permission prmsn) {
			return true;
		}

		public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
			return new PermissionAttachment(HungerGames.getInstance(), this);
		}

		public PermissionAttachment addAttachment(Plugin plugin) {
			return new PermissionAttachment(HungerGames.getInstance(), this);
		}

		public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
			return new PermissionAttachment(HungerGames.getInstance(), this);
		}

		public PermissionAttachment addAttachment(Plugin plugin, int i) {
			return new PermissionAttachment(HungerGames.getInstance(), this);
		}

		public void removeAttachment(PermissionAttachment pa) {
		}

		public void recalculatePermissions() {
		}

		public Set<PermissionAttachmentInfo> getEffectivePermissions() {
			return new HashSet<PermissionAttachmentInfo>();
		}

		public boolean isOp() {
			return true;
		}

		public void setOp(boolean bln) {
		}
		
	}
}
