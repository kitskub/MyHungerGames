package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class RejoinCommand extends PlayerCommand {

	public RejoinCommand() {
		super(Perm.USER_REJOIN, "rejoin", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		game = HungerGames.getInstance().getGameManager().getRawSession(player);
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
	protected String getPrivateUsage() {
		return "rejoin";
	}
    
}
