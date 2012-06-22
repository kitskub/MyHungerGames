package com.randude14.hungergames.commands;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand extends SubCommand{

	@Override
	public boolean execute(CommandSender cs, Command cmd, String[] args) {
		Player player = (Player) cs;
		if (!HungerGames.checkPermission(player, Perm.USER_LIST)) return true;

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
