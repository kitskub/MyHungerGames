package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RejoinCommand extends SubCommand{

	public RejoinCommand() {
		super(Commands.USER_REJOIN);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.getSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return true;
		}
		game.rejoin(player);
		return true;
	}
    
}
