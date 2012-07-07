package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.utils.ChatUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand extends SubCommand{

	public VoteCommand() {
		super(Commands.USER_VOTE);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		game = GameManager.getSession(player);
		if (game == null) {
			ChatUtils.error(player, "You must be in a game to vote. You can a game join by '" 
				+ Commands.USER_JOIN.getUsage() + "'", HungerGames.CMD_USER);
			return true;
		}
		game.addReadyPlayer(player);
		ChatUtils.send(player, "You have voted that you are ready.");
		return true;
	}
    
}
