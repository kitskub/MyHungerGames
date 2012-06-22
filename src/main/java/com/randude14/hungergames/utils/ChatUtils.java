package com.randude14.hungergames.utils;

import com.randude14.hungergames.Logging;
import com.randude14.hungergames.Plugin;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
	
	public static void info(String format, Object... args) {
		Logging.log(Level.INFO, getLogPrefix() + String.format(format, args));
	}

	public static void info(String mess) {
		Logging.log(Level.INFO, getLogPrefix() + mess);
	}

	public static void warning(String format, Object... args) {
		Logging.log(Level.WARNING, getLogPrefix() + String.format(format, args));
	}

	public static void warning(String mess) {
		Logging.log(Level.WARNING, getLogPrefix() + mess);
	}

	public static void severe(String format, Object... args) {
		Logging.log(Level.SEVERE, getLogPrefix() + String.format(format, args));
	}

	public static void severe(String mess) {
		Logging.log(Level.SEVERE, getLogPrefix() + mess);
	}

	public static String getLogPrefix() {
		return String.format("[%s] v%s - ", Plugin.getInstance().getName(), Plugin.getInstance().getDescription().getVersion());
	}

	public static String getPrefix() {
		return String.format("[%s] - ", Plugin.getInstance().getName());
	}

	public static String getHeadLiner() {
		return String.format("--------------------[%s]--------------------", Plugin.getInstance().getName());
	}

	public static void broadcast(String message) {
		broadcast(message, ChatColor.GREEN);
	}

	public static void broadcast(String format, Object... args) {
		broadcast(String.format(format, args));
	}

	public static void broadcast(ChatColor color, String format, Object... args) {
		broadcast(color, String.format(format, args));
	}

	public static void broadcast(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + getPrefix() + message);
		}

		message = ChatColor.stripColor(message);
		info(message);
	}

	public static void broadcastRaw(ChatColor color, String format, Object... args) {
		broadcastRaw(String.format(format, args), color);
	}

	public static void broadcastRaw(String message) {
		broadcastRaw(message, ChatColor.GREEN);
	}

	public static void broadcastRaw(String message, ChatColor color) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(color + message);
		}

		message = ChatColor.stripColor(message);
		Logging.log(Level.INFO, message);
	}

	public static void send(Player player, ChatColor color, String format, Object... args) {
		player.sendMessage(color + String.format(format, args));
	}

	public static void send(Player player, ChatColor color, String mess) {
		player.sendMessage(color + mess);
	}

	public static void send(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GRAY + String.format(format, args));
	}

	public static void send(Player player, String mess) {
		player.sendMessage(ChatColor.GRAY + mess);
	}

	public static void help(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GOLD + String.format(format, args));
	}

	public static void help(Player player, String mess) {
		player.sendMessage(ChatColor.GOLD + mess);
	}

	public static void helpCommand(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.GOLD + String.format("- " + format, args));
	}

	public static void error(Player player, String format, Object... args) {
		player.sendMessage(ChatColor.RED + String.format(format, args));
	}

	public static void error(Player player, String mess) {
		player.sendMessage(ChatColor.RED + mess);
	}

	public static void sendDoesNotExist(Player player, String s) {
		error(player, "%s does not exist.", s);
	}	
}
