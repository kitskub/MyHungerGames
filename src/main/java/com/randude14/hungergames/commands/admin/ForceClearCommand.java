package com.randude14.hungergames.commands.admin;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
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
			ChatUtils.helpCommand(cs, getPrivateUsage(), HungerGames.CMD_ADMIN);
			return;
		}
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getPrivateUsage(), HungerGames.CMD_ADMIN);
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
