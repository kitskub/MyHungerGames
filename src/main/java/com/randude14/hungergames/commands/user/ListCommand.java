package com.randude14.hungergames.commands.user;

import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.commands.Command;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command {

	public ListCommand() {
		super(Perm.USER_LIST, "list", USER_COMMAND);
	}

	@Override
	public void handle(CommandSender cs, String cmd, String[] args) {
		ChatUtils.send(cs, ChatColor.GREEN, ChatUtils.getHeadLiner());
		Collection<HungerGame> games = GameManager.INSTANCE.getRawGames();
		if (games.isEmpty()) {
			ChatUtils.error(cs, "No games have been created yet.");
			return;
		}

		for (HungerGame g : games) {
			ChatUtils.send(cs, ChatColor.GOLD, "- " + g.getInfo());
		}
	}

	@Override
	public String getInfo() {
		return "list games";
	}

	@Override
	public String getUsage() {
		return "/%s list";
	}
    
}
