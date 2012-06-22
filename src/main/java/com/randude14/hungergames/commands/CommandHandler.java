package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.CommandUsage;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang.ArrayUtils;


public class CommandHandler implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("In-game use only.");
			return true;
		}
		if (cmd.getLabel().equalsIgnoreCase(HungerGames.CMD_USER)) {
			handleUserCommand((Player) sender, cmd, args);
		} else if (cmd.getLabel().equalsIgnoreCase(HungerGames.CMD_ADMIN)) {
			handleAdminCommand((Player) sender, cmd, args);
		}
		return false;
	}

	private void handleUserCommand(Player player, Command cmd, String[] args) {
		SubCommand command = null;
		if (args.length == 0) {
			if (!HungerGames.checkPermission(player, Perm.USER_HELP)) return;
			
			getUserCommands(player, cmd);
			return;
		}

		else if ("list".equalsIgnoreCase(args[0])) {
			command = new ListCommand();
		}
		else if ("join".equalsIgnoreCase(args[0])) {
			command = new JoinCommand();
		}
		else if ("leave".equalsIgnoreCase(args[0])) {
			command = new LeaveCommand();
		}
		else if ("quit".equalsIgnoreCase(args[0])) {
			command = new QuitCommand();
		}
		else if ("rejoin".equalsIgnoreCase(args[0])) {
			command = new RejoinCommand();
		}
		else if("spectate".equalsIgnoreCase(args[0])) {
			command = new SpectateCommand();
		}
		else if ("sponsor".equalsIgnoreCase(args[0])) {
			command = new SponsorCommand();
		}
		else if ("vote".equalsIgnoreCase(args[0])) {
			command = new VoteCommand();
		}
		else if ("stat".equalsIgnoreCase(args[0])) {
			command = new StatCommand();
		}
		else {
			if (!HungerGames.checkPermission(player, Perm.USER_HELP)) return;

			getUserCommands(player, cmd);
		}
		if (command != null) {
			command.execute(player, cmd, (String[]) ArrayUtils.removeElement(args, args[0]));
			command.save();
		}
	}

	private void handleAdminCommand(Player player, Command cmd, String[] args) {
		SubCommand command = null;
		if (args.length == 0) {
			if (!HungerGames.hasPermission(player, Perm.ADMIN_HELP)) return;
			
			getAdminCommands(player, cmd);
			return;
		}

		if ("add".equalsIgnoreCase(args[0])) {
			command = new AddCommand();
		}
		else if ("remove".equalsIgnoreCase(args[0])) {
			command = new RemoveCommand();
		}
		else if ("set".equalsIgnoreCase(args[0])) {
			command = new SetCommand();
		}
		else if ("restock".equalsIgnoreCase(args[0])) {
			command = new RestockCommand();
		}
		else if("pause".equalsIgnoreCase(args[0])) {
			command = new PauseCommand();
		}
		else if("resume".equalsIgnoreCase(args[0])) {
			command = new ResumeCommand();
		}
		else if ("kick".equalsIgnoreCase(args[0])) {
			command = new KickCommand();
		}
		else if ("start".equalsIgnoreCase(args[0])) {
			command = new StartCommand();
		}
		else if ("reload".equalsIgnoreCase(args[0])) {
			command = new ReloadCommand();
		}
		else {
			if (!HungerGames.checkPermission(player, Perm.ADMIN_HELP)) return;
			
			getAdminCommands(player, cmd);
		}
		if (command != null) {
			command.execute(player, cmd, (String[]) ArrayUtils.removeElement(args, args[0]));
			command.save();
		}
	}

	private void getUserCommands(Player player, Command cmd) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		ChatUtils.helpCommand(player, CommandUsage.USER_LIST.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_JOIN.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_LEAVE.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_QUIT.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_REJOIN.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_SPONSOR.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_VOTE.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_STAT.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.USER_SPECTATE.getUsageAndInfo(), cmd.getLabel());
	}

	private void getAdminCommands(Player player, Command cmd) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_ADD_HELP.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_REMOVE_HELP.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_SET_HELP.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_KICK.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_RELOAD.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_RESTOCK.getUsageAndInfo(), cmd.getLabel());
		ChatUtils.helpCommand(player, CommandUsage.ADMIN_START.getUsageAndInfo(), cmd.getLabel());
	}

}
