package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand extends SubCommand{

	public StatCommand() {
		super(Commands.USER_STAT);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.send(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		game = GameManager.getGame(args[0]);
		if (game == null) {
			ChatUtils.sendDoesNotExist(player, args[0]);
			return true;
		}
		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		game.listStats(player);
		return true;
	}
    
}
