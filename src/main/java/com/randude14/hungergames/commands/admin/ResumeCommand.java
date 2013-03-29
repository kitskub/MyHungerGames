package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Defaults;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class ResumeCommand extends Command {

	public ResumeCommand() {
		super(Perm.ADMIN_RESUME, "resume", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		
		String name = (args.length < 1) ? Defaults.Config.DEFAULT_GAME.getGlobalString() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}	

		game = HungerGames.getInstance().getGameManager().getRawGame(name);
		if (game == null) {
		    ChatUtils.error(cs, "%s does not exist.", name);
		    return;
		}

		if(args.length < 2) {//TODO WRONG!
			if(!game.resumeGame(cs, false)) {
				ChatUtils.error(cs, "Failed to resume %s.", game.getName());
			}

		}
		else {
			int seconds;
			try {
				seconds = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				ChatUtils.error(cs, "'%s' is not an integer.", args[1]);
				return;
			}
			if(!game.resumeGame(cs, seconds)) {
				ChatUtils.error(cs, "Failed to resume %s.", game.getName());
			}

		}
		return;
	}

	@Override
	public String getInfo() {
		return "resume a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "resume [game name]";
	}
    
}
