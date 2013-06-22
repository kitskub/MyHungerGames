package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Commands;
import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.games.HungerGame;
import me.kitskub.hungergames.games.User;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class VoteCommand extends PlayerCommand {

	public VoteCommand() {
		super(Perm.USER_VOTE, "vote", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {
		User get = User.get(player);
		game = get.getGameInEntry().getGame();
		if (game == null) {
			ChatUtils.error(player, "You must be in a game to vote. You can a game join by '" + Commands.USER_JOIN.getCommand().getUsage() + "'", HungerGames.CMD_USER);
			return;
		}
		((HungerGame) game).addReadyPlayer(player);
		ChatUtils.send(player, Lang.getVoted(game.getSetup()));
	}

	@Override
	public String getInfo() {
		return "cast your vote that you are ready to play";
	}

	@Override
	protected String getPrivateUsage() {
		return "vote";
	}
    
}
