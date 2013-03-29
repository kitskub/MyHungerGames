package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatCommand extends Command {

	public StatCommand() {
		super(Perm.USER_STAT, "stat", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {		
		if((args.length < 1)){
			// No game given. Take game payer currently is in
			Player player = Bukkit.getServer().getPlayer(cs.getName());
			if((player instanceof Player)){
				game = HungerGames.getInstance().getGameManager().getRawSession(player);
			}
			if (game == null) {
				ChatUtils.helpCommand(cs, getUsage(), HungerGames.CMD_USER);
				return;
			}
		} else {
			// Game given. Check if game exists
			game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
			if (game == null) {
				ChatUtils.error(cs, Lang.getNotExist().replace("<item>", args[0]));
				return;
			}
		}	
		
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		game.listStats(cs);
	}

	@Override
	public String getInfo() {
		return "list stats for a game";
	}

	@Override
	protected String getPrivateUsage() {
		return "stat [game name]";
	}
    
}
