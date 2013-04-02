package me.kitskub.hungergames.commands.user;

import me.kitskub.hungergames.Defaults.Perm;
import me.kitskub.hungergames.GameManager;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Lang;
import me.kitskub.hungergames.commands.PlayerCommand;
import me.kitskub.hungergames.utils.ChatUtils;

import org.bukkit.entity.Player;

public class SubscribeCommand extends PlayerCommand {

	public SubscribeCommand() {
		super(Perm.USER_SUBSCRIBE, "subscribe", USER_COMMAND);
	}

	@Override
	public void handlePlayer(Player player, String cmd, String[] args) {		
		if (args.length > 0) {
			game = HungerGames.getInstance().getGameManager().getRawGame(args[0]);
			if (game == null) {
				ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
				return;
			}
		}
		if (HungerGames.getInstance().getGameManager().isPlayerSubscribed(player, game)) {
			HungerGames.getInstance().getGameManager().removedSubscribedPlayer(player, game);
			ChatUtils.send(player, "You have been unsubscribed from those MyHungerGames messages.");
		}
		else {
			HungerGames.getInstance().getGameManager().addSubscribedPlayer(player, game);
			ChatUtils.send(player, "You have been subscribed to those MyHungerGames messages.");
		}
	}

	@Override
	public String getInfo() {
		return "subscribe to game messages";
	}

	@Override
	protected String getPrivateUsage() {
		return "subscribe [game]";
	}
	
}
