package com.randude14.hungergames.utils;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.HungerGamesBukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

	// TODO convert to CommandSender
	public static String getPrefix() {
		return String.format("[%s] - ", HungerGamesBukkit.getInstance().getName());
	}

	public static String getHeadLiner() {
		return String.format("-------------------[%s]--------------------", HungerGamesBukkit.getInstance().getName());
	}

	public static void broadcast(ChatColor color, String message, boolean subscribedOnly) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (Config.getAllowMinimalMessagesGlobal() && !GameManager.INSTANCE.isPlayerSubscribed(player)) continue;
			player.sendMessage(color + getPrefix() + message);
		}

		message = ChatColor.stripColor(message);
		Logging.info(message);
	}

	public static void broadcast(String message, boolean subscribedOnly) {
		broadcast(ChatColor.GREEN, message, subscribedOnly);
	}

	public static void broadcast(boolean subscribedOnly, String format, Object... args) {
		broadcast(String.format(format, args), subscribedOnly);
	}

	public static void broadcast(boolean subscribedOnly, ChatColor color, String format, Object... args) {
		broadcast(color, String.format(format, args), subscribedOnly);
	}

	public static void broadcastRaw(String message, ChatColor color, boolean subscribedOnly) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (Config.getAllowMinimalMessagesGlobal() && !GameManager.INSTANCE.isPlayerSubscribed(player)) continue;
			player.sendMessage(color + message);
		}

		message = ChatColor.stripColor(message);
		Logging.info(message);
	}
		
	public static void broadcastRaw(boolean subscribedOnly, ChatColor color, String format, Object... args) {
		broadcastRaw(String.format(format, args), color, subscribedOnly);
	}

	public static void broadcastRaw(String message, boolean subscribedOnly) {
		broadcastRaw(message, ChatColor.GREEN, subscribedOnly);
	}


	public static void send(CommandSender cs, ChatColor color, String mess) {
		cs.sendMessage(color + mess);
	}
	
	public static void send(Player player, ChatColor color, String format, Object... args) {
		send(player, color, String.format(format, args));
	}
			
	public static void send(Player player, String mess) {
		send(player, ChatColor.GRAY, mess);
	}
	
	public static void send(Player player, String format, Object... args) {
		send(player, ChatColor.GRAY, String.format(format, args));
	}
	

	public static void help(CommandSender cs, String mess) {
		send(cs, ChatColor.GOLD, mess);
	}
	
	public static void help(Player player, String format, Object... args) {
		help(player, String.format(format, args));
	}
	
	public static void helpCommand(CommandSender cs, String format, Object... args) {
		help(cs, String.format("- " + format, args));
	}
	
	
	public static void error(Player player, String mess) {
		send(player, ChatColor.RED, mess);
	}

	public static void error(Player player, String format, Object... args) {
		error(player, String.format(format, args));
	}	
}
