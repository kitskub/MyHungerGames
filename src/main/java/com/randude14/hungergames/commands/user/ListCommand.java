package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Commands;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.SubCommand;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand{

	public ListCommand() {
		super(Commands.USER_LIST);
	}

	@Override
	public boolean handle(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;

		ChatUtils.send(player, ChatColor.GREEN, ChatUtils.getHeadLiner());
		Collection<HungerGame> games = GameManager.getGames();
		if (games.isEmpty()) {
			ChatUtils.error(player, "No games have been created yet.");
			return true;
		}

		for (HungerGame g : games) {
			ChatUtils.send(player, ChatColor.GOLD, "- " + g.getInfo());
		}
		return true;
	}
    
}
