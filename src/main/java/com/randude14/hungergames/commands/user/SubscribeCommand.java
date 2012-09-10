package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Lang;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubscribeCommand extends Command {

	public SubscribeCommand() {
		super(Perm.USER_SUBSCRIBE, "subscribe", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;
		
		if (args.length > 0) {
			game = GameManager.INSTANCE.getRawGame(args[0]);
			if (game == null) {
				ChatUtils.error(player, Lang.getNotExist().replace("<item>", args[0]));
				return;
			}
		}
		if (GameManager.INSTANCE.getSubscribedPlayers(game).contains(player.getName())) {
			GameManager.INSTANCE.removedSubscribedPlayer(player, game);
			ChatUtils.send(player, "You have been unsubscribed from those MyHungerGames messages.");
		}
		else {
			GameManager.INSTANCE.addSubscribedPlayer(player, game);
			ChatUtils.send(player, "You have been subscribed to those MyHungerGames messages.");
		}
	}

	@Override
	public String getInfo() {
		return "subscribe to game messages";
	}

	@Override
	public String getUsage() {
		return "/%s subscribe [game]";
	}
	
}
