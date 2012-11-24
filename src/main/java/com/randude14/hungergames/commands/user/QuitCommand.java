package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.PlayerCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class QuitCommand extends PlayerCommand {

	public QuitCommand() {
		super(Perm.USER_QUIT, "quit", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		game = GameManager.INSTANCE.getRawSession(player);
		if (game == null) {
			ChatUtils.error(player, "You are currently not in a game.");
			return;
		}

		game.quit(player, true);
	}

	@Override
	public String getInfo() {
		return "quit the current game indefinitely";
	}

	@Override
	protected String getPrivateUsage() {
		return "quit";
	}
    
}
