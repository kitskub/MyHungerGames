package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.stats.SQLStat;
import com.randude14.hungergames.stats.StatHandler;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;

public class SearchCommand extends Command {

	public SearchCommand() {
		super(Perm.USER_SEARCH, "search", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
		if (args.length < 1) {
			ChatUtils.error(cs, "Must have a player to search for!");
			return;
		}
		SQLStat stat = StatHandler.getStat(args[0]);
		if (stat == null) {
			ChatUtils.send(cs, "Could not find stat for: %s", args[0]);
		}
		ChatUtils.send(cs, ChatUtils.getHeadLiner());
		ChatUtils.send(cs, "Player stat for: %s", args[0]);
		ChatUtils.send(cs, "%s has a global rank of %s",args[0], stat.rank);
		ChatUtils.send(cs, "%s has played %s games for a total of %s", args[0], stat.totalGames, stat.totalTime);
		ChatUtils.send(cs, "%s has had %s wins, %s deaths, and %s kills", args[0], stat.wins, stat.deaths, stat.kills);
	}

	@Override
	public String getInfo() {
		return "searches for a player's stat and prints out the info";
	}

	@Override
	public String getUsage() {
		return "/%s search <player>";
	}

}
