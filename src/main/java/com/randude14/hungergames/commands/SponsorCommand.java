package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SponsorCommand extends SubCommand{

	@Override
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (!HungerGames.checkPermission(player, Perm.USER_SPONSOR)) return true;

		if (args.length < 1) {
			ChatUtils.send(player, Commands.USER_SPONSOR.getUsage(), cmd.getLabel());
			return true;
		}

		Player p = Bukkit.getServer().getPlayer(args[0]);
		if (p == null) {
			ChatUtils.error(player, "%s is not online.", args[0]);
			return true;
		}
		if (GameManager.getPlayingSession(p) == null) {
			ChatUtils.error(player, "%s is not playing in a game.", p.getName());
			return true;
		}
		GameManager.addSponsor(player, p);
		return true;
	}
    
}
