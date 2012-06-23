package com.randude14.hungergames.commands;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand{

	@Override
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (!HungerGames.checkPermission(player, Perm.USER_JOIN)) return true;

		String name = (args.length < 1) ? Config.getDefaultGame() : args[0];
		if (name == null) {
			ChatUtils.helpCommand(player, Commands.USER_JOIN.getUsage(), cmd.getLabel());
			return true;
		}

		game = GameManager.getGame(name);
		if (game == null) {
			ChatUtils.sendDoesNotExist(player, name);
			return true;
		}

		HungerGame currentSession = GameManager.getSession(player);
		if (currentSession != null) {
			ChatUtils.error(player, 
				"You are already in the game '%s'. Leave that game before joining another.",
				currentSession.getName());
			return true;
		}
		if (game.join(player)) {
			String mess = Config.getJoinMessage(game.getSetup());
			mess = mess.replace("<player>", player.getName()).replace("<game>", game.getName());
			ChatUtils.broadcast(mess);
		}
		return true;
	}
}
