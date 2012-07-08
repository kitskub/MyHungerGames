package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends SubCommand{

	public StartCommand() {
		super(Commands.ADMIN_START);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		if (args.length < 1) {
			ChatUtils.helpCommand(player, command.getUsage(), cmd.getLabel());
			return true;
		}

		game = GameManager.getGame(args[0]);
		if (game == null) {
			ChatUtils.sendDoesNotExist(player, args[0]);
			return true;
		}

		int seconds;

		if (args.length == 2) {
			try {
				seconds = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				ChatUtils.error(player, "'%s' is not an integer.", args[1]);
				return true;
			}
		}

		else {
			seconds = Config.getDefaultTime(game.getSetup());
		}
		if (!game.startGame(player, seconds, true)) {
			ChatUtils.error(player, "Failed to start %s.", game.getName());
		}
		return true;
	}
    
}
