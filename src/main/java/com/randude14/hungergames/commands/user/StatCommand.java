package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StatCommand extends Command {

	public StatCommand() {
		super(Perm.USER_STAT, "stat", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {		
		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_USER);
			return;
		}

		game = GameManager.INSTANCE.getRawGame(name);
		if (game == null) {
			ChatUtils.error(cs, Lang.getNotExist().replace("<item>", name));
			return;
		}
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		game.listStats(cs);
	}

	@Override
	public String getInfo() {
		return "list stats for a game";
	}

	@Override
	public String getUsage() {
		return "/%s stat [game name]";
	}
    
}
