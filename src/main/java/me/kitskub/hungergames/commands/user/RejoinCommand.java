package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

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
