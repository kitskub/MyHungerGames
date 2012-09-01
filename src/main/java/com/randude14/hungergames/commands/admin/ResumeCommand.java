package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResumeCommand extends Command {

	public ResumeCommand() {
		super(Perm.ADMIN_RESUME, "resume", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		Player player = (Player) cs;
		
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}	

		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
		    ChatUtils.error(player, "%s does not exist.", name);
		    return;
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
				return;
			}
			if(!game.resumeGame(player, seconds)) {
				ChatUtils.error(player, "Failed to resume %s.", game.getName());
			}

		}
		return;
	}

	@Override
	public String getInfo() {
		return "resume a game";
	}

	@Override
	public String getUsage() {
		return "/%s resume [game name]";
	}
    
}
