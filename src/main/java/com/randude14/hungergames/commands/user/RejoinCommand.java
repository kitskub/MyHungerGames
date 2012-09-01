package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RejoinCommand extends Command {

	public RejoinCommand() {
		super(Perm.USER_REJOIN, "rejoin", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.INSTANCE.getRawSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return;
		}
		game.rejoin(player);
	}

	@Override
	public String getInfo() {
		return "rejoin your current game";
	}

	@Override
	public String getUsage() {
		return "/%s rejoin";
	}
    
}
