package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends Command {

	public StartCommand() {
		super(Commands.ADMIN_START, "start", ADMIN_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;

		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return true;
		}
		game = GameManager.INSTANCE.getGame(name);
		if (game == null) {
			ChatUtils.error(player, Lang.getNotExist().replace("<item>", name));
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
		if (!game.startGame(player, seconds)) {
			ChatUtils.error(player, "Failed to start %s.", game.getName());
		}
		return true;
	}

	@Override
	public String getInfo() {
		return "manually start a game";
	}

	@Override
	public String getUsage() {
		return "/%s start [<game name> [seconds]]";
	}
    
}
