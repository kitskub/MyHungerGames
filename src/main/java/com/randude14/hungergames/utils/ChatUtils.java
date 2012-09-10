package com.randude14.hungergames.utils;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

	// TODO convert to CommandSender
	public static String getPrefix() {
		return String.format("[%s] - ", HungerGames.getInstance().getName());
	}

	public static String getHeadLiner() {
		return String.format("-------------------[%s]--------------------", HungerGames.getInstance().getName());
	}
	
	public static void broadcast(Game game, ChatColor color, String message) {
		broadcastRaw(game, color, getPrefix() + message);
	}

	public static void broadcast(Game game, String message) {
		broadcast(game, ChatColor.GREEN, message);
	}

	public static void broadcast(Game game, String format, Object... args) {
		broadcast(game, String.format(format, args));
	}

	public static void broadcast(Game game, ChatColor color, String format, Object... args) {
		broadcast(game, color, String.format(format, args));
	}

	/**
	 * Same as broadcasted but with no prefix
	 * @param message
	 * @param color
	 * @param game 
	 */
	public static void broadcastRaw(Game game, ChatColor color, String message) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (Config.getAllowMinimalMessagesGlobal() && !GameManager.INSTANCE.isPlayerSubscribed(player, game)) continue;
			player.sendMessage(color + message);
		}

		message = ChatColor.stripColor(message);
		Logging.info(message);
	}
		
	public static void broadcastRaw(Game game, ChatColor color, String format, Object... args) {
		broadcastRaw(game, color, String.format(format, args));
	}

	public static void broadcastRaw(Game game, String message) {
		broadcastRaw(game, ChatColor.GREEN, message);
	}


	public static void send(CommandSender cs, ChatColor color, String mess) {
		cs.sendMessage(color + mess);
	}
	
	public static void send(CommandSender cs, ChatColor color, String format, Object... args) {
		send(cs, color, String.format(format, args));
	}
			
	public static void send(CommandSender cs, String mess) {
		send(cs, ChatColor.GRAY, mess);
	}
	
	public static void send(CommandSender cs, String format, Object... args) {
		send(cs, ChatColor.GRAY, String.format(format, args));
	}
	

	public static void help(CommandSender cs, String mess) {
		send(cs, ChatColor.GOLD, mess);
	}
	
	public static void help(CommandSender cs, String format, Object... args) {
		help(cs, String.format(format, args));
	}
	
	public static void helpCommand(CommandSender cs, String format, Object... args) {
		help(cs, String.format("- " + format, args));
	}
	
	
	public static void error(CommandSender cs, String mess) {
		send(cs, ChatColor.RED, mess);
	}

	public static void error(CommandSender cs, String format, Object... args) {
		error(cs, String.format(format, args));
	}	
}
