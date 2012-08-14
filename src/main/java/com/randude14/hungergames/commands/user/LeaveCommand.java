package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends SubCommand{

	public LeaveCommand() {
		super(Commands.USER_LEAVE);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.getPlayingSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not playing a game.");
			return true;
		}

		game.leave(player, true);
		return true;
	}
}
