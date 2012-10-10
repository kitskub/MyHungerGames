package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang.ArrayUtils;


public class CommandHandler implements CommandExecutor {
	public static CommandHandler INSTANCE = new CommandHandler();
	public static Map<String, Command> adminCommands = new HashMap<String, Command>();
	public static Map<String, Command> userCommands = new HashMap<String, Command>();

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (cmd.getLabel().equalsIgnoreCase(HungerGames.CMD_USER)) {
			handleUserCommand((Player) sender, cmd, args);
		} else if (cmd.getLabel().equalsIgnoreCase(HungerGames.CMD_ADMIN)) {
			handleAdminCommand((Player) sender, cmd, args);
		}
		return false;
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

	private void handleUserCommand(CommandSender cs, org.bukkit.command.Command cmd, String[] args) {
		Command command = null;
		if (args.length == 0 || (command = userCommands.get(args[0].toLowerCase())) == null) {
			if (!HungerGames.checkPermission(cs, Perm.USER_HELP)) return;
			getUserCommands(cs, cmd);
			return;
		}
		command.execute(cs, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		command.save();
	}

	private void handleAdminCommand(Player player, org.bukkit.command.Command cmd, String[] args) {
		Command command = null;
		if (args.length == 0 || (command = adminCommands.get(args[0].toLowerCase())) == null) {
			if (!HungerGames.hasPermission(player, Perm.ADMIN_HELP)) return;
			getAdminCommands(player, cmd);
			return;
		}
		command.execute(player, args[0], (String[]) ArrayUtils.removeElement(args, args[0]));
		command.save();
	}

	private void getUserCommands(CommandSender cs, org.bukkit.command.Command cmd) {
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		for (Command c : userCommands.values()) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

	private void getAdminCommands(CommandSender cs, org.bukkit.command.Command cmd) {
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		for (Command c : adminCommands.values()) {
			ChatUtils.helpCommand(cs, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

}
