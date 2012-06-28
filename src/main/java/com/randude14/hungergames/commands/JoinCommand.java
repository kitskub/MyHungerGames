package com.randude14.hungergames.commands;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand{

	public JoinCommand() {
		super(Commands.USER_JOIN);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		game = GameManager.getGame(name);
		if (game == null) {
			ChatUtils.sendDoesNotExist(player, name);
			return true;
		}

		game.join(player);
		return true;
	}
}
