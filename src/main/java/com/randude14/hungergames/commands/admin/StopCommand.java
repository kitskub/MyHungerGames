package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class StopCommand extends Command {

	public StopCommand() {
		super(Perm.ADMIN_STOP, "stop", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {		
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		
		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
		    ChatUtils.error(cs, "%s does not exist.", name);
		    return;
		}
		if (!game.checkForGameOver(false)) game.stopGame(cs, false);
	}

	@Override
	public String getInfo() {
		return "manually stop a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "stop [game name]";
	}
    
}
