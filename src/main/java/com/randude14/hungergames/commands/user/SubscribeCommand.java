package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubscribeCommand extends Command {

	public SubscribeCommand() {
		super(Perm.USER_SUBSCRIBE, "subscribe", USER_COMMAND);
	}

	@Override
	public boolean handle(CommandSender cs, String cmd, String[] args) {
		Player player = (Player) cs;

		if (GameManager.INSTANCE.isPlayerSubscribed(player)) {
			GameManager.INSTANCE.removedSubscribedPlayer(player);
			ChatUtils.send(player, "You have been unsubscribed from MyHungerGames messages.");
		}
		else {
			GameManager.INSTANCE.addSubscribedPlayer(player);
			ChatUtils.send(player, "You have been subscribed to MyHungerGames messages.");
		}
		return true;
	}

	@Override
	public String getInfo() {
		return "subscribe to game messages";
	}

	@Override
	public String getUsage() {
		return "/%s subscribe";
	}
	
}
