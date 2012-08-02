package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.stats.SQLStat;
import com.randude14.hungergames.stats.StatHandler;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SearchCommand extends SubCommand {

	public SearchCommand() {
		super(Commands.USER_SEARCH);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (args.length < 1) {
			ChatUtils.error(player, "Must have a player to search for!");
			return true;
		}
		SQLStat stat = StatHandler.getStat(args[0]);
		if (stat == null) {
			ChatUtils.send(player, "Could not find stat for: %s", args[0]);
		}
		ChatUtils.send(player, ChatUtils.getHeadLiner());
		ChatUtils.send(player, "Player stat for: %s", args[0]);
		ChatUtils.send(player, "%s has a global rank of %s",args[0], stat.rank);
		ChatUtils.send(player, "%s has played %s games for a total of %s", args[0], stat.totalGames, stat.totalTime);
		ChatUtils.send(player, "%s has had %s wins, %s deaths, and %s kills", args[0], stat.wins, stat.deaths, stat.kills);
		return true;
	}

}
