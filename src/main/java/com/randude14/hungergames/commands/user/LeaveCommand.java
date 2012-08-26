package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends Command {

	public LeaveCommand() {
		super(Commands.USER_LEAVE, "leave", USER_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.INSTANCE.getPlayingSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not playing a game.");
			return true;
		}

		game.leave(player, true);
		return true;
	}

	@Override
	public String getInfo() {
		return "leave current game temporarily (if enabled)";
	}

	@Override
	public String getUsage() {
		return "/%s leave";
	}
}
