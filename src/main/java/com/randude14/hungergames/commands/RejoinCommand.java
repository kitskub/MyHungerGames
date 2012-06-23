package com.randude14.hungergames.commands;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RejoinCommand extends SubCommand{

	@Override
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (!HungerGames.checkPermission(player, Perm.USER_REJOIN)) return true;

		game = GameManager.getSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return true;
		}

		if (game.rejoin(player)) {
			String mess = Config.getRejoinMessage(game.getSetup());
			mess = mess.replace("<player>", player.getName()).replace("<game>", game.getName());
			ChatUtils.broadcast(mess);
		}
		else {
			ChatUtils.error(player, "Failed to rejoin %s.", game.getName());
		}
		return true;
	}
    
}
