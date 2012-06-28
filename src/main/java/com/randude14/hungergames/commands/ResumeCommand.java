package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResumeCommand extends SubCommand{

	public ResumeCommand() {
		super(Commands.ADMIN_RESUME);
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
		    ChatUtils.error(player, "%s does not exist.", args[0]);
		    return true;
		}

		if(args.length == 1) {
			if(!game.resumeGame(player, false)) {
				ChatUtils.error(player, "Failed to resume %s.", game.getName());
			}

		}

		else {
			int seconds;
			try {
				seconds = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				ChatUtils.error(player, "'%s' is not an integer.", args[1]);
				return true;
			}
			if(!game.resumeGame(player, seconds)) {
				ChatUtils.error(player, "Failed to resume %s.", game.getName());
			}

		}
		return true;
	}
    
}
