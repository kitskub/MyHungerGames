package com.randude14.hungergames.utils;

import com.randude14.hungergames.*;
import com.randude14.hungergames.core.CommandSender;
import com.randude14.hungergames.core.LocalPlayer;

import org.bukkit.ChatColor;

public class ChatUtils {

	// TODO convert to CommandSender
	public static String getPrefix() {
		return String.format("[%s] - ", HungerGamesBukkit.getInstance().getName());
	}

	public static String getHeadLiner() {
		return String.format("-------------------[%s]--------------------", HungerGamesBukkit.getInstance().getName());
	}

	public static void broadcast(ChatColor color, String message, boolean subscribedOnly) {
		for (LocalPlayer player : HungerGames.getPlugin().getServerInterface().getOnlinePlayers()) {
			if (Config.getAllowMinimalMessagesGlobal() && !GameManager.INSTANCE.isPlayerSubscribed(player)) continue;
			player.send(color + getPrefix() + message);
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
		for (LocalPlayer player : HungerGames.getPlugin().getServerInterface().getOnlinePlayers()) {
			if (Config.getAllowMinimalMessagesGlobal() && !GameManager.INSTANCE.isPlayerSubscribed(player)) continue;
			player.send(color + message);
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
		cs.send(color + mess);
	}
	
	public static void send(LocalPlayer player, ChatColor color, String format, Object... args) {
		send(player, color, String.format(format, args));
	}
			
	public static void send(LocalPlayer player, String mess) {
		send(player, ChatColor.GRAY, mess);
	}
	
	public static void send(CommandSender cs, String format, Object... args) {
		send(cs, ChatColor.GRAY, String.format(format, args));
	}
	

	public static void help(CommandSender cs, String mess) {
		send(cs, ChatColor.GOLD, mess);
	}
	
	public static void help(LocalPlayer player, String format, Object... args) {
		help(player, String.format(format, args));
	}
	
	public static void helpCommand(CommandSender cs, String format, Object... args) {
		help(cs, String.format("- " + format, args));
	}
	
	
	public static void error(CommandSender sender, String mess) {
		send(sender, ChatColor.RED, mess);
	}

	public static void error(LocalPlayer player, String format, Object... args) {
		error(player, String.format(format, args));
	}	
}
