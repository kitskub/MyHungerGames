package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.*;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class ForceClearCommand extends Command {

	public ForceClearCommand() {
		super(Perm.ADMIN_FORCE_CLEAR, "forceclear", ADMIN_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String label, String[] args) {
		if (args.length < 1) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		String name = (args.length < 1) ? Defaults.Config.DEFAULT_GAME.getGlobalString() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
			ChatUtils.error(cs, Lang.getNotExist().replace("<item>", name));
			return;
		}
		ChatUtils.send(cs, "Clearing game.");
		game.clear();
	}

	@Override
	public String getInfo() {
		return "force clear a game. NOT RECOMMENDED";
	}

	@Override
	protected String getPrivateUsage() {
		return "forceclear <game name>";
	}
    
}
