package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubscribeCommand extends SubCommand{

	public SubscribeCommand() {
		super(Commands.USER_SUBSCRIBE);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		if (GameManager.isPlayerSubscribed(player)) {
			GameManager.removedSubscribedPlayer(player);
			ChatUtils.send(player, "You have been unsubscribed from MyHungerGames messages.");
		}
		else {
			GameManager.addSubscribedPlayer(player);
			ChatUtils.send(player, "You have been subscribed to MyHungerGames messages.");
		}
		return true;
	}
	
}
