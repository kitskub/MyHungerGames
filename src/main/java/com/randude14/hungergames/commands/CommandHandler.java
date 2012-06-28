package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
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
		else if ("subscribe".equalsIgnoreCase(args[0])) {
			command = new SubscribeCommand();
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
			if (args.length == 1 || "?".equalsIgnoreCase(args[1])) {
				ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_SPAWNPOINT.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_CUBOID.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_CHEST.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_GAME.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_ITEMSET.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_ADD_WORLD.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				return;
			}
			if (args[1].equalsIgnoreCase("spawnpoint")) command = new AddSpawnPointCommand();
			else if (args[1].equalsIgnoreCase("cuboid")) command = new AddCuboidCommand();
			else if (args[1].equalsIgnoreCase("chest")) command = new AddChestCommand();
			else if (args[1].equalsIgnoreCase("game")) command = new AddGameCommand();
			else if (args[1].equalsIgnoreCase("itemset")) command = new AddItemSetCommand();
			else if (args[1].equalsIgnoreCase("world")) command = new AddWorldCommand();
			else {
				ChatUtils.error(player, "Command not recognized.");
				return;
			}
			ArrayUtils.removeElement(args, args[0]); // Need to remove that extra arg
		}
		else if ("remove".equalsIgnoreCase(args[0])) {
			if (args.length == 1 || "?".equalsIgnoreCase(args[1])) {
				ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
				ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_SPAWNPOINT.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_CHEST.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_GAME.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_REMOVE_ITEMSET.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				return;
			}
			if (args[1].equalsIgnoreCase("spawnpoint")) command = new RemoveSpawnPointCommand();
			else if (args[1].equalsIgnoreCase("chest")) command = new RemoveChestCommand();
			else if (args[1].equalsIgnoreCase("game")) command = new RemoveGameCommand();
			else if (args[1].equalsIgnoreCase("itemset")) command = new RemoveItemSetCommand();
			else {
				ChatUtils.error(player, "Command not recognized.");
				return;
			}
			ArrayUtils.removeElement(args, args[0]); // Need to remove that extra arg
		}
		else if ("set".equalsIgnoreCase(args[0])) {
			if (args.length == 1 || "?".equalsIgnoreCase(args[1])) {
				ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
				ChatUtils.helpCommand(player, Commands.ADMIN_SET_SPAWN.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				ChatUtils.helpCommand(player, Commands.ADMIN_SET_ENABLED.getUsageAndInfo(), HungerGames.CMD_ADMIN);
				return;
			}
			if (args[1].equalsIgnoreCase("spawn")) command = new SetSpawnCommand();
			else if (args[1].equalsIgnoreCase("enabled")) command = new SetEnabledCommand();
			else {
				ChatUtils.error(player, "Command not recognized.");
				return;
			}
			ArrayUtils.removeElement(args, args[0]); // Need to remove that extra arg
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
		else if ("stop".equalsIgnoreCase(args[0])) {
			command = new StopCommand();
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
		for (Commands c : Commands.values()) {
			if (!c.getType().equals(Commands.ADMIN_COMMAND)) continue;
			ChatUtils.helpCommand(player, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

	private void getAdminCommands(Player player, Command cmd) {
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		for (Commands c : Commands.values()) {
			if (!c.getType().equals(Commands.ADMIN_COMMAND)) continue;
			ChatUtils.helpCommand(player, c.getUsageAndInfo(), cmd.getLabel());
		}
	}

}
