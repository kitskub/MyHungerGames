package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class QuitCommand extends PlayerCommand {

	public QuitCommand() {
		super(Perm.USER_QUIT, "quit", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		game = HungerGames.getInstance().getGameManager().getRawSession(player);
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
