package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.HungerGamesBukkit;
import com.randude14.hungergames.HungerGamesSpout;
import com.randude14.hungergames.core.LocalPlayer;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
//import ;

import org.apache.commons.lang.ArrayUtils;

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;


public class CommandHandler implements org.bukkit.command.CommandExecutor, org.spout.api.command.CommandExecutor {
	public static CommandHandler INSTANCE = new CommandHandler();
	public static Map<String, Command> adminCommands = new HashMap<String, Command>();
	public static Map<String, Command> userCommands = new HashMap<String, Command>();

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (!(sender instanceof org.bukkit.entity.Player)) {
			sender.sendMessage("In-game use only.");
			return true;
		}
		if (cmd.getLabel().equalsIgnoreCase(HungerGamesBukkit.CMD_USER)) {
			handleUserCommand(LocalPlayer.fromCommandSender(sender), cmd, args);
		} else if (cmd.getLabel().equalsIgnoreCase(HungerGamesBukkit.CMD_ADMIN)) {
			handleAdminCommand(LocalPlayer.fromCommandSender(sender), cmd, args);
		}
		return false;
	}

	public void processCommand(CommandSource source, org.spout.api.command.Command command, CommandContext args) throws CommandException {
		if (!(source instanceof LocalPlayer)) {
			source.sendMessage("In-game use only.");
			return;
		}
		if (args.getCommand().equalsIgnoreCase(HungerGamesSpout.CMD_USER)) {
			handleUserCommand(LocalPlayer.fromCommandSource(source), command, args.getJoinedString(0).asString().split(" "));
		} else if (args.getCommand().equalsIgnoreCase(HungerGamesSpout.CMD_ADMIN)) {
			handleAdminCommand(LocalPlayer.fromCommandSource(source), command, args.getJoinedString(0).asString().split(" "));
		}
	}

	public static void registerUserCommand(Command command) {
		userCommands.put(command.getName(), command);
		for (String s : command.getAliases()) {
			userCommands.put(s.toLowerCase(), command);
		}
	}
	
	public static void registerAdminCommand(Command command) {
		adminCommands.put(command.getName(), command);
		for (String s : command.getAliases()) {
			adminCommands.put(s.toLowerCase(), command);
		}
	}

	private void handleUserCommand(LocalPlayer player, Command cmd, String[] args) {
		Command command = null;
		if (args.length == 0 || (command = userCommands.get(args[0].toLowerCase())) == null) {
			if (!HungerGames.checkPermission(player, Perm.USER_HELP)) return;
			getUserCommands(player, cmd);
			return;
		}
		command.execute(player, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		command.save();
	}

	private void handleAdminCommand(LocalPlayer player, Command cmd, String[] args) {
		Command command = null;
		if (args.length == 0 || (command = adminCommands.get(args[0].toLowerCase())) == null) {
			if (!HungerGames.hasPermission(player, Perm.ADMIN_HELP)) return;
			getAdminCommands(player, cmd);
			return;
		}
		command.execute(player, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		command.save();
	}

	private void getUserCommands(LocalPlayer player, org.bukkit.command.Command cmd) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		for (Command c : userCommands.values()) {
			ChatUtils.helpCommand(player, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

	private void getAdminCommands(LocalPlayer player, org.bukkit.command.Command cmd) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		for (Command c : adminCommands.values()) {
			ChatUtils.helpCommand(player, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

}
