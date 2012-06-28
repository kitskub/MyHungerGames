package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitCommand extends SubCommand{

	public QuitCommand() {
		super(Commands.USER_QUIT);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.getSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return true;
		}

		game.quit(player);
		return true;
	}
    
}
