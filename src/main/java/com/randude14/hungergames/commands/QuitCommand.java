package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitCommand extends SubCommand{

	@Override
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (!HungerGames.checkPermission(player, Perm.USER_QUIT)) return true;

		game = GameManager.getSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return true;
		}

		game.quit(player);
		return true;
	}
    
}
