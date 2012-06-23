package com.randude14.hungergames.utils;

import com.randude14.hungergames.Logging;
import com.randude14.hungergames.HungerGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

	public static String getPrefix() {
		return String.format("[%s] - ", HungerGames.getInstance().getName());
	}

	public static String getHeadLiner() {
		return String.format("--------------------[%s]--------------------", HungerGames.getInstance().getName());
	}

	public static void broadcast(ChatColor color, String message) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + getPrefix() + message);
		}

		message = ChatColor.stripColor(message);
		Logging.info(message);
	}

	public static void broadcast(String message) {
		broadcast(ChatColor.GREEN, message);
	}

	public static void broadcast(String format, Object... args) {
		broadcast(String.format(format, args));
	}

	public static void broadcast(ChatColor color, String format, Object... args) {
		broadcast(color, String.format(format, args));
	}

	public static void broadcastRaw(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + message);
		}

		message = ChatColor.stripColor(message);
		Logging.info(message);
	}
		
	public static void broadcastRaw(ChatColor color, String format, Object... args) {
		broadcastRaw(String.format(format, args), color);
	}

	public static void broadcastRaw(String message) {
		broadcastRaw(message, ChatColor.GREEN);
	}


	public static void send(Player player, ChatColor color, String mess) {
		player.sendMessage(color + mess);
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
	

	public static void help(Player player, String mess) {
		send(player, ChatColor.GOLD, mess);
	}
	
	public static void help(Player player, String format, Object... args) {
		help(player, String.format(format, args));
	}
	
	public static void helpCommand(Player player, String format, Object... args) {
		help(player, String.format("- " + format, args));
	}
	
	
	public static void error(Player player, String mess) {
		send(player, ChatColor.RED, mess);
	}

	public static void error(Player player, String format, Object... args) {
		error(player, String.format(format, args));
	}
	
	public static void sendDoesNotExist(Player player, String s) {
		error(player, "%s does not exist.", s);
	}	
}
