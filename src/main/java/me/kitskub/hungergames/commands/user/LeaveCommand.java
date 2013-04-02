package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class LeaveCommand extends PlayerCommand {

	public LeaveCommand() {
		super(Perm.USER_LEAVE, "leave", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		game = HungerGames.getInstance().getGameManager().getRawPlayingSession(player);
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
	protected String getPrivateUsage() {
		return "leave";
	}
}
