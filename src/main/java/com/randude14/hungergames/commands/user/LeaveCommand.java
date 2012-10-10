package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class LeaveCommand extends PlayerCommand {

	public LeaveCommand() {
		super(Perm.USER_LEAVE, "leave", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		game = GameManager.INSTANCE.getRawPlayingSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not playing a game.");
			return;
		}

		game.leave(player, true);
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
